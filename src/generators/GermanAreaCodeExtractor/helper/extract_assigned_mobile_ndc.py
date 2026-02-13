
# Website for used mobile NDCs: https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/MobileDienste/zugeteilteRNB/start.html

def mobile_ndcs(filename='mobile_ndcs.html'):
    with open(filename, newline='') as f:
        data = f.read().replace('\n', '')
        data = data.split("<caption>Liste der zugeteilten Rufnummernblöcke / Mobile Dienste</caption>")[1]
        data = data.split("<tbody>")[1]
        data = data.split("</tbody>")[0]
        data = data.split("</th>")[2]

        data = data.replace('<tr class="odd">', "")
        data = data.replace('<tr class="even">', "")
        data = data.replace('<td class="xl24" colspan="1" rowspan="1">', "")
        data = data.replace('<td class="xl28" colspan="1" rowspan="1">', "")
        data = data.replace('</tr>', "{+}")
        data = data.replace('                                                ', "")
        data = data.replace('<abbr title="Gesellschaft mit beschränkter Haftung">', "")
        data = data.replace('<abbr title="Offene Handelsgesellschaft">', "")
        data = data.replace('<abbr title="Gesellschaft mit beschränkter Haftung &amp; Compagnie">', "")
        data = data.replace('<abbr title="mit beschränkter Haftung">', "")
        data = data.replace('<abbr title="Compagnie">', "")
        data = data.replace('</abbr>', "")
        data = data.replace('<td colspan="1" rowspan="1">(0)', "")
        data = data.replace('<td class="xl24" colspan="1" rowspan="1">(0)', "")
        data = data.replace('<td class="xl27" colspan="1" rowspan="1">(0)', "")
        data = data.replace('</td><td class="xl24" colspan="1" rowspan="1">', ",")
        data = data.replace('</td><td class="xl28" colspan="1" rowspan="1">', ",")
        data = data.replace('</td>', "{:}")
        data = data.replace('&amp;', "&")
        data = data.replace('  ', " ")
        data = data.replace('  ', " ")
        data = data.replace('  ', " ")
        data = data.replace('  ', " ")
        data = data.replace('  ', " ")
        data = data.replace(" (0)", "")
        data = data.replace('15-', "15")

        data = data.replace('{:}  ', "{:}")
        data = data.replace('{:}{+}', "{+}")
        data = data.replace('  {+}', "{+}")
        data = data.replace('{+} ', "{+}")
        data = data.replace(' {:}', "{:}")

        data = data.replace('  ', " ")

        mf_ndcs = data.split('{+}')

    return mf_ndcs
