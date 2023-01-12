SUMMARY = "Patchpanel network connectivity management D-Bus client"
DESCRIPTION = "Patchpanel network connectivity management D-Bus client"
HOMEPAGE = "https://chromium.googlesource.com/chromiumos/platform2/+/HEAD/patchpanel/dbus/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"

inherit chromeos_gn

CHROMEOS_PN = "patchpanel/dbus"

S = "${WORKDIR}/src/platform2/${CHROMEOS_PN}"
B = "${WORKDIR}/build"
PR = "r342"

DEPENDS:append = "\
    libbrillo \
    libchrome \
    system-api \
    protobuf \
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
PACKAGECONFIG[arcvm] = ""
PACKAGECONFIG[cros_host] = ""
PACKAGECONFIG[fuzzer] = ""
PACKAGECONFIG[profiling] = ""
PACKAGECONFIG[tcmalloc] = ""
PACKAGECONFIG[test] = ""

GN_ARGS += ' \
    use={ \
        arcvm=${@bb.utils.contains('PACKAGECONFIG', 'arcvm', 'true', 'false', d)} \
        cros_host=${@bb.utils.contains('PACKAGECONFIG', 'cros_host', 'true', 'false', d)} \
        fuzzer=${@bb.utils.contains('PACKAGECONFIG', 'fuzzer', 'true', 'false', d)} \
        profiling=${@bb.utils.contains('PACKAGECONFIG', 'profiling', 'true', 'false', d)} \
        tcmalloc=${@bb.utils.contains('PACKAGECONFIG', 'tcmalloc', 'true', 'false', d)} \
        test=${@bb.utils.contains('PACKAGECONFIG', 'test', 'true', 'false', d)} \
    } \
'

do_install() {
    ( cd "${S}"; "${S}"/preinstall.sh ${PR} /usr/include/chromeos "${B}" )
    install -d ${D}${libdir}/pkgconfig
    install -m 0644 libpatchpanel-client.pc ${D}${libdir}/pkgconfig/

    install -d ${D}${libdir}
    install -m 0644 lib/libpatchpanel-client.so ${D}${libdir}/libpatchpanel-client.so.${SO_VERSION}
    ln -sf libpatchpanel-client.so.${SO_VERSION} ${D}${libdir}/libpatchpanel-client.so

    install -d ${D}${includedir}/chromeos/patchpanel/dbus
    for i in client.h fake_client.h; do
        sed '/.pb.h/! s:patchpanel/:chromeos/patchpanel/:g' "${S}/$i" > "${D}"/usr/include/chromeos/patchpanel/dbus/"$i"
    done
}
