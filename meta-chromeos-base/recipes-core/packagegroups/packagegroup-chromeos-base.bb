SUMMARY = "Minimal chromeos-base components"
DESCRIPTION = "The minimal set of packages required to emulate chromeos-base base-image"
PR = "r1"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

# Mostly from target-chromium-os.
RDEPENDS:${PN} = "\
    chromeos-init \
    chromeos-login \
    kmod \
    mosys \
    regions \
    rsyslog \
    shill \
    trunks \
    u2fd \
    vtpm \
"
# TODO: update-engine; need puffin, bsdiff.

# From implicit-system.
RDEPENDS:${PN} += "\
    dash \
    ca-certificates \
    coreutils \
    findutils \
    grep \
    gawk \
    sed \
    which \
    procps \
    net-tools \
    shadow \
    util-linux \
"
