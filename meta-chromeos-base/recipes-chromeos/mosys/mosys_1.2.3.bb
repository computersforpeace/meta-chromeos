SUMMARY = "Utility for obtaining various bits of low-level system info"
DESCRIPTION = "Utility for obtaining various bits of low-level system info"
HOMEPAGE = "http://mosys.googlecode.com/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"

inherit chromeos_gn meson
CHROMEOS_PN = "mosys"
require recipes-chromeos/chromiumos-platform/chromiumos-platform-${CHROMEOS_PN}.inc

# cmocka doesn't include all its headers, and mosys doesn't quite get them all
# right either.
SRC_URI += "file://cmocka-stdint.patch"

S = "${WORKDIR}/src/platform/${CHROMEOS_PN}"
B = "${WORKDIR}/build"
PR = "r1498"

DEPENDS += "\
    cmocka \
    libminijail \
"
RDEPENDS:${PN} = "\
    libminijail \
"

PACKAGECONFIG ??= ""

# Description of all the possible PACKAGECONFIG fields (comma delimited):
# 1. Extra arguments that should be added to the configure script argument list (EXTRA_OECONF or PACKAGECONFIG_CONFARGS) if the feature is enabled.
# 2. Extra arguments that should be added to EXTRA_OECONF or PACKAGECONFIG_CONFARGS if the feature is disabled.
# 3. Additional build dependencies (DEPENDS) that should be added if the feature is enabled.
# 4. Additional runtime dependencies (RDEPENDS) that should be added if the feature is enabled.
# 5. Additional runtime recommendations (RRECOMMENDS) that should be added if the feature is enabled.
# 6. Any conflicting (that is, mutually exclusive) PACKAGECONFIG settings for this feature.\n
# Empty PACKAGECONFIG options listed here to avoid warnings.
# The .bb file should use these to conditionally add patches,
# command-line switches and dependencies.
PACKAGECONFIG[unibuild] = ""
#PACKAGECONFIG[${PLATFORM_NAME_USE_FLAGS[*]}] = ""

GN_ARGS += ' \
    use={ \
        unibuild=${@bb.utils.contains('PACKAGECONFIG', 'unibuild', 'true', 'false', d)} \
        ${PLATFORM_NAME_USE_FLAGS[*]}=${@bb.utils.contains('PACKAGECONFIG', '${PLATFORM_NAME_USE_FLAGS[*]}', 'true', 'false', d)} \
    } \
'

# TODO: check toolchain arch?
EXTRA_OEMESON:append = "\
    -Darch=x86 \
    -Dunibuild=${@bb.utils.contains('PACKAGECONFIG', 'unibuild', 'true', 'false', d)} \
"

do_compile() {
    meson compile
}

FILES:${PN} += "${datadir}/policy"
do_install() {
    install -d ${D}${sbindir}
    install -m 0755 mains/mosys ${D}${sbindir}

    ARCH=${TARGET_ARCH}
    [ "${ARCH}" = x86_64 ] && ARCH=amd64

    install -d ${D}${datadir}/policy
    install -m 0644 ${S}/seccomp/mosys-seccomp-${ARCH}.policy ${D}${datadir}/policy/mosys-seccomp.policy
}
