SUMMARY = "Shill networking component interface library"
DESCRIPTION = "Shill networking component interface library"
HOMEPAGE = "https://chromium.googlesource.com/chromiumos/platform2/+/HEAD/shill/net"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"

inherit chromeos_gn

CHROMEOS_PN = "shill/net"

S = "${WORKDIR}/src/platform2/${CHROMEOS_PN}"
B = "${WORKDIR}/build"
PR = "r1034"

DEPENDS:append = "\
    session-manager-client \
    openssl \
    protobuf \
    system-api \
    libbrillo \
    libchrome \
    re2 \
"

GN_ARGS += 'platform_subdir="${CHROMEOS_PN}"'

PACKAGECONFIG ??= ""

# Description of all the possible PACKAGECONFIG fields (comma delimited):
# 1. Extra arguments that should be added to the configure script argument list (EXTRA_OECONF or PACKAGECONFIG_CONFARGS) if the feature is enabled.
# 2. Extra arguments that should be added to EXTRA_OECONF or PACKAGECONFIG_CONFARGS if the feature is disabled.
# 3. Additional build dependencies (DEPENDS) that should be added if the feature is enabled.
# 4. Additional runtime dependencies (RDEPENDS) that should be added if the feature is enabled.
# 5. Additional runtime recommendations (RRECOMMENDS) that should be added if the feature is enabled.
# 6. Any conflicting (that is, mutually exclusive) PACKAGECONFIG settings for this feature.

# Empty PACKAGECONFIG options listed here to avoid warnings.
# The .bb file should use these to conditionally add patches,
# command-line switches and dependencies.
PACKAGECONFIG[cros_host] = ""
PACKAGECONFIG[fuzzer] = ""
PACKAGECONFIG[profiling] = ""
PACKAGECONFIG[tcmalloc] = ""
PACKAGECONFIG[test] = ""
PACKAGECONFIG[wifi] = ""

GN_ARGS += ' \
    use={ \
        cros_host=${@bb.utils.contains('PACKAGECONFIG', 'cros_host', 'true', 'false', d)} \
        fuzzer=${@bb.utils.contains('PACKAGECONFIG', 'fuzzer', 'true', 'false', d)} \
        profiling=${@bb.utils.contains('PACKAGECONFIG', 'profiling', 'true', 'false', d)} \
        tcmalloc=${@bb.utils.contains('PACKAGECONFIG', 'tcmalloc', 'true', 'false', d)} \
        test=${@bb.utils.contains('PACKAGECONFIG', 'test', 'true', 'false', d)} \
        wifi=${@bb.utils.contains('PACKAGECONFIG', 'wifi', 'true', 'false', d)} \
    } \
'

do_compile() {
    ninja -C ${B}
}

do_install() {
    ( cd "${S}"; ./preinstall.sh "${B}" 1 )
    install -d ${D}${libdir}/pkgconfig
    install -m 0644 lib/libshill-net.pc ${D}${libdir}/pkgconfig/

    install -d ${D}${libdir}
    install -m 0644 lib/libshill-net.so ${D}${libdir}/libshill-net.so.${SO_VERSION}
    ln -sf libshill-net.so.${SO_VERSION} ${D}${libdir}/libshill-net.so

    install -d ${D}${includedir}/shill/net
    install -m 0644 ${S}/*.h ${D}${includedir}/shill/net/
}
