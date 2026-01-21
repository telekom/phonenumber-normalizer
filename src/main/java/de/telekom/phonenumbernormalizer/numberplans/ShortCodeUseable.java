package de.telekom.phonenumbernormalizer.numberplans;

public enum ShortCodeUseable {

    WITH_IDP_AND_CC_FROM_OUTSIDE(),
    WITH_IDP_AND_CC_AND_NDC_FROM_OUTSIDE(),
    WITH_IDP_AND_CC_FROM_INSIDE(),
    WITH_IDP_AND_CC_AND_NDC_FROM_INSIDE(),
    WITH_NAC(),
    WITH_NAC_AND_NDC(),
    DIRECTLY();
}
