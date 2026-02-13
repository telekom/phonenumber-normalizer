import csv

last_ndc = "xxx"


def add(leaf, keypart, name):
    if len(keypart) == 1:
        leaf[keypart] = name
    else:
        if not keypart[0] in leaf:
            leaf[keypart[0]] = {}
        add(leaf[keypart[0]], keypart[1:], name)


def print_function(leaf, prefix):
    for k in leaf:
        if isinstance(leaf[k], dict):
            print_function(leaf[k], prefix + k)
        else:
            ndc = prefix+k
            l = 7
            if ndc.startswith("15"):
                l = 11 - len(ndc)
            if ndc == '176':
                l = 8
            if ndc == '160':
                print('                Map.entry("' + ndc + '", new NDCDetails(7, 8, false, 1)), // ' + leaf[k])
                print('                // NDC 160 uses first digit of number for deviating ranges with different length')
                for i in range(10):
                    if i == 9:
                        l = 8
                    else:
                        l = 7
                    print('                Map.entry("' + ndc + str(i) +'", new NDCDetails(' + str(l) + ', ' + str(l) + ', false)), // ' + leaf[k])
            else:
                if ndc == last_ndc:
                    print('                Map.entry("' + ndc + '", new NDCDetails(' + str(l) + ', ' + str(l) + ', false)) // ' + leaf[k])
                else:
                    print('                Map.entry("'+ndc+'", new NDCDetails('+str(l)+', '+str(l)+', false)), // '+ leaf[k])




# Start, creating a dictonary for placing the Numberplan as a tree
onkz = {}

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
            last_ndc = ndc[0]
            add(onkz, ndc[0], ndc[1])

onkz = dict(sorted(onkz.items()))

# print code from three
print_function(onkz, "")





