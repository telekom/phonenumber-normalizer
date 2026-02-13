
def insert_voicemail_infix(mobile_ndc):
    if mobile_ndc.startswith("151") or mobile_ndc.startswith("160") or mobile_ndc.startswith("170") or \
            mobile_ndc.startswith("171") or mobile_ndc.startswith("175"):
        if len(mobile_ndc) < 3:
            return []
        return [mobile_ndc[:3] + "13" + mobile_ndc[3:]]

    if mobile_ndc.startswith("162") or mobile_ndc.startswith("172") or mobile_ndc.startswith("173") or \
            mobile_ndc.startswith("174"):
        if len(mobile_ndc) < 3:
            return []
        return [mobile_ndc[:3] + "50" + mobile_ndc[3:], mobile_ndc[:3] + "55" + mobile_ndc[3:]]

    if mobile_ndc.startswith("152"):
        if len(mobile_ndc) < 4:
            return []
        return [mobile_ndc[:4] + "50" + mobile_ndc[4:], mobile_ndc[:4] + "55" + mobile_ndc[4:]]

    if mobile_ndc.startswith("150") or mobile_ndc.startswith("153") or mobile_ndc.startswith("154") or \
            mobile_ndc.startswith("155") or mobile_ndc.startswith("156") or mobile_ndc.startswith("158"):
        if len(mobile_ndc) < 3:
            return []
        return [mobile_ndc[:3] + "00" + mobile_ndc[3:]]

    if mobile_ndc.startswith("163") or mobile_ndc.startswith("177") or mobile_ndc.startswith("178"):
        if len(mobile_ndc) < 3:
            return []
        return [mobile_ndc[:3] + "99" + mobile_ndc[3:]]

    if mobile_ndc.startswith("157"):
        if len(mobile_ndc) < 4:
            return []
        return [mobile_ndc[:4] + "99" + mobile_ndc[4:]]

    if mobile_ndc.startswith("176") or mobile_ndc.startswith("179") :
        if len(mobile_ndc) < 3:
            return []
        return [mobile_ndc[:3] + "33" + mobile_ndc[3:]]

    if mobile_ndc.startswith("159"):
        if len(mobile_ndc) < 4:
            return []
        return [mobile_ndc[:4] + "33" + mobile_ndc[4:]]

    return []


