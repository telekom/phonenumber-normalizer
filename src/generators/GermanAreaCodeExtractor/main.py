import csv


def add(leaf, keypart, name):
    if len(keypart) == 1:
        leaf[keypart] = name
    else:
        if not keypart[0] in leaf:
            leaf[keypart[0]] = {}
        add(leaf[keypart[0]], keypart[1:], name)


def print_function(leaf, prefix):
    if prefix == '':
        java_visibility = 'public'
    else:
        java_visibility = 'private'
    print('  '+java_visibility+' static String fromNumber'+ prefix +'(String number) {')
    print('    if ((number == null) || (number.length()<1)) {')
    print('      return "";')
    print('    }')
    print('')
    print('    switch (number.charAt(0)) {')

    for k in leaf:
        print("      case '"+k+"':")

        if isinstance(leaf[k], dict):
            print('        return fromNumber'+prefix+k+'(number.substring(1));')
        else:
            if (prefix+k) == "212":
                print('        // special edge case, see: https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sach'
                      'gebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/ONVerzeichnisse/ONBV'
                      'erzeichnis/Sonderregelungen0212_0621.pdf?__blob=publicationFile&v=1')
                print('        if ((number.length() > 1) && (number.substring(1, 2).equals("9"))) {')
                print('            return "2129"; // Haan Rheinland')
                print('        }')
            print('        return "'+prefix+k+'"; // '+ leaf[k])

    print('      default:')
    print('        return "";')
    print('    }')
    print('  }')
    print('')

    for k in leaf:
        if isinstance(leaf[k], dict):
            print_function(leaf[k], prefix + k)

# Start, creating a dictonary for placing the Numberplan as a tree
onkz = {}

add(onkz, "199", "special NDC for German Operators internal use")

# Data from https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Telekommunikation/Unternehmen_Institutionen/Nummerierung/Rufnummern/ONRufnr/Vorwahlverzeichnis_ONB.zip.zip?__blob=publicationFile&v=1
# it is linked at https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/ONRufnr/Einteilung_ONB/start.html

with open('NVONB.INTERNET.20220727.ONB.csv', newline='') as csvfile:
    reader = csv.reader(csvfile, delimiter=';', quotechar='"')
    for row in reader:
        # remove first line: Ortsnetzkennzahl;Ortsnetzname;KennzeichenAktiv
        if row == ['Ortsnetzkennzahl', 'Ortsnetzname', 'KennzeichenAktiv']:
            continue
        # remove line: 2129;Haan Rheinl;1 // because of overlapping, this is added explicitly, see above
        if row == ['2129', 'Haan Rheinl', '1']:
            continue
        # remove last line: 
        if row == ['\x1a']:
            continue
        add(onkz, row[0], row[1])

# Website for used mobile NDCs: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/zugeteilte%20RNB/start.html
with open('mobile_ndcs.html', newline='') as f:
    data = f.read().replace('\n', '')
    data = data.split("<caption>Liste der zugeteilten Rufnummernblöcke / Mobile Dienste</caption>")[1]
    data = data.split("<tbody>")[1]
    data = data.split("</tbody>")[0]
    data = data.split("</th>")[2]

    data = data.replace('<tr class="odd">', "")
    data = data.replace('<tr class="even">', "")
    data = data.replace('</tr>', "")
    data = data.replace('                                                ', "")
    data = data.replace('<abbr title="Gesellschaft mit beschränkter Haftung">', "")
    data = data.replace('<abbr title="Offene Handelsgesellschaft">', "")
    data = data.replace('<abbr title="Gesellschaft mit beschränkter Haftung &amp; Compagnie">', "")
    data = data.replace('<abbr title="mit beschränkter Haftung">', "")
    data = data.replace('<abbr title="mit beschränkter Haftung">', "")
    data = data.replace('</abbr>', "")
    data = data.replace('<td colspan="1" rowspan="1">(0)', "")
    data = data.replace('<td class="xl24" colspan="1" rowspan="1">(0)', "")
    data = data.replace('<td class="xl27" colspan="1" rowspan="1">(0)', "")
    data = data.replace('</td><td class="xl24" colspan="1" rowspan="1">', ",")
    data = data.replace('</td><td class="xl28" colspan="1" rowspan="1">', ",")
    data = data.replace('</td>', "{+}")
    data = data.replace('&amp;', "&")
    data = data.replace('  ', " ")
    data = data.replace('  ', " ")
    data = data.replace(', ', ",")
    data = data.replace(',', "{:}")

    data = data.replace('15-', "15")
    mf_ndcs = data.split('{+}')

    for mf_ndc in mf_ndcs:
        ndc = mf_ndc.split('{:}')
        if len(ndc) == 2:
            add(onkz, ndc[0], ndc[1])

onkz = dict(sorted(onkz.items()))

# print code from three
print_function(onkz, "")





