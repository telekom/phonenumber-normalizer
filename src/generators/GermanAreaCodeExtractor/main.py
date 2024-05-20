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
    print('    switch (number.substring(0, 1)) {')

    if prefix == "":
        # main function - need explicit reference to service and mobile function for starting numbers with 1
        print('      case "1":')
        print('        return fromNumber1(number.substring(1));')

    for k in leaf:
        print('      case "'+k+'":')

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

# print code from three
print_function(onkz, "")





