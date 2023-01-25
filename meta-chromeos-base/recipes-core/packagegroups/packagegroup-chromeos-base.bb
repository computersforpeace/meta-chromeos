SUMMARY = "Minimal chromeos-base components"
DESCRIPTION = "The minimal set of packages required to emulate chromeos-base base-image"
PR = "r1"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

# Mostly from target-chromium-os.
RDEPENDS:${PN} = "\
    bash \
    chromeos-init \
    chromeos-login \
    e2fsprogs \
    iproute2 \
    kmod \
    mosys \
    permission-broker \
    regions \
    rsyslog \
    shill \
    tar \
    trunks \
    u2fd \
    udev \
    vboot-reference \
    vpd \
    vtpm \
"
# TODO: update-engine; need puffin, bsdiff.

# To get kernel modules:
RDEPENDS:${PN} += "kernel-modules"

# HACK: probably needs to be generated and included by some other means.
RDEPENDS:${PN} += "write-gpt"

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
