import csv
from helper.extract_assigned_mobile_ndc import mobile_ndcs
from helper.voicemail_infix import insert_voicemail_infix

def add(leaf, keypart, name):
    if len(keypart) == 1:
        leaf[keypart] = name
    else:
        key = keypart[0]
        if not key in leaf:
            leaf[key] = {}
        if isinstance(leaf[key], str):
            print(key)
        else:
            add(leaf[key], keypart[1:], name)


def generate_extractor_code(leaf, prefix):
    code = ""
    if prefix == '':
        java_visibility = 'public'
    else:
        java_visibility = 'private'
    code += '  ' + java_visibility + ' static String fromNumber' + prefix + '(String number) {\n'
    code += '    if ((number == null) || (number.length()<1)) {\n'
    code += '      return "";\n'
    code += '    }\n'
    code += '\n'
    code += '    switch (number.charAt(0)) {\n'

    for k in leaf:
        code += "      case '"+k+"':\n"

        if isinstance(leaf[k], dict):
            code += '        return fromNumber'+prefix+k+'(number.substring(1));\n'
        else:

            new_ndc = prefix+k

            for overlaping_ndc in overlaping_ndcs.keys():
                if overlaping_ndc.startswith(new_ndc):
                    extension_length = len(overlaping_ndc) - len(new_ndc)
                    extension = overlaping_ndc[-extension_length:]
                    code += '        // Overlapping NDC ' + overlaping_ndc + '\n'
                    code += '        if ((number.length() > ' + str(extension_length) + ') && '
                    code += '(number.substring(1, ' + str(extension_length + 1) + ').equals("' + extension + '"))) {\n'
                    code += '            return "' + overlaping_ndc + '"; // ' + overlaping_ndcs[overlaping_ndc] + '\n'
                    code += '        }\n'
            code += '        return "' + new_ndc + '"; // ' + leaf[k] + '\n'

    code += '      default:\n'
    code += '        return "";\n'
    code += '    }\n'
    code += '  }\n'
    code += '\n'

    for k in leaf:
        if isinstance(leaf[k], dict):
            code += generate_extractor_code(leaf[k], prefix + k)

    return code


def is_ndc_overlapping(possible_overlapping_ndc):
    for ndc in ndcs.keys():
        if possible_overlapping_ndc.startswith(ndc):
            return True
    return False


# Start, creating a dictonary for placing the Numberplan as a tree
onkz = {}

overlaping_ndcs = {}
ndcs = {}

add(onkz, "137", "mass traffic")
add(onkz, "199", "special NDC for German Operators internal use")
add(onkz, "700", "personal phone numbers")

# Data from https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/ONRufnr/Vorwahlverzeichnis_ONB.zip.zip?__blob=publicationFile&v=1
# it is linked at https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/ONRufnr/Einteilung_ONB/start.html

with open('NVONB.INTERNET.20220727.ONB.csv', newline='') as csvfile:
    reader = csv.reader(csvfile, delimiter=';', quotechar='"')
    for row in reader:
        # remove first line: Ortsnetzkennzahl;Ortsnetzname;KennzeichenAktiv
        if row == ['Ortsnetzkennzahl', 'Ortsnetzname', 'KennzeichenAktiv']:
            continue
        # remove last line: 
        if row == ['\x1a']:
            continue

        # generic overlapping to remove line: 2129;Haan Rheinl;1
        if is_ndc_overlapping(row[0]):
            overlaping_ndcs[row[0]] = row[1]
        else:
            ndcs[row[0]] = row[1]
            add(onkz, row[0], row[1])

# Website for used mobile NDCs: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/zugeteilteRNB/start.html

mf_ndcs = mobile_ndcs('helper/mobile_ndcs.html')

for mf_ndc in mf_ndcs:
    ndc = mf_ndc.split('{:}')
    if len(ndc) == 2:
        ndcs[ndc[0]] = ndc[1]
        add(onkz, ndc[0], ndc[1])
        voicemal_infixes = insert_voicemail_infix(ndc[0])
        for voicemail_infix in voicemal_infixes:
            lore = "Voicemail Infix inserted for " + ndc[1]
            # if voicemail_infix.startswith(ndc[0]) or voicemail_infix.startswith("151"):
            if is_ndc_overlapping(voicemail_infix):
                overlaping_ndcs[voicemail_infix] = lore
            else:
                ndcs[voicemail_infix] = lore
                add(onkz, voicemail_infix, lore)


# print(overlaping_ndc)

onkz = dict(sorted(onkz.items()))

# print code from three
print(generate_extractor_code(onkz, ""))





