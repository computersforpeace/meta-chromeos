SUMMARY = "Library to run jailed containers on Chrome OS"
DESCRIPTION = "Library to run jailed containers on Chrome OS"
HOMEPAGE = "https://chromium.googlesource.com/chromiumos/platform2/+/HEAD/libcontainer/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"

inherit chromeos_gn eapi

S = "${WORKDIR}/src/platform2/${BPN}"
B = "${WORKDIR}/build"
PR = "r1689"

DEPENDS += "libbrillo libchrome libminijail"
RDEPENDS:${PN} += "libbrillo libchrome libminijail"

GN_ARGS += 'platform_subdir="${BPN}"'

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
PACKAGECONFIG[device_mapper] = ""
PACKAGECONFIG[profiling] = ""
PACKAGECONFIG[tcmalloc] = ""
PACKAGECONFIG[test] = ""

GN_ARGS += ' \
    use={ \
        cros_host=${@bb.utils.contains('PACKAGECONFIG', 'cros_host', 'true', 'false', d)} \
        device_mapper=${@bb.utils.contains('PACKAGECONFIG', 'device_mapper', 'true', 'false', d)} \
        fuzzer=${@bb.utils.contains('PACKAGECONFIG', 'fuzzer', 'true', 'false', d)} \
        profiling=${@bb.utils.contains('PACKAGECONFIG', 'profiling', 'true', 'false', d)} \
        tcmalloc=${@bb.utils.contains('PACKAGECONFIG', 'tcmalloc', 'true', 'false', d)} \
        test=${@bb.utils.contains('PACKAGECONFIG', 'test', 'true', 'false', d)} \
    } \
'

do_compile() {
    ninja -C ${B}
}

do_install() {
    #inherit() { :; }
    #source ${FILE_DIRNAME}/libcontainer.ebuild
    #into
    #src_install
    OUT=${B}
    PV=1.0
    get_libdir() { basename "${libdir}"; }
    cd "${S}"

    into /
    INTO=/
    dolib_so "${OUT}"/lib/libcontainer.so

    "${S}"/platform2_preinstall.sh "${PV}" "/usr/include/chromeos" "${OUT}"
    #insinto "/usr/$(get_libdir)/pkgconfig"
    INSINTO="/usr/$(get_libdir)/pkgconfig"
    doins "${OUT}"/libcontainer.pc

    #insinto "/usr/include/chromeos"
    INSINTO="/usr/include/chromeos"
    doins libcontainer.h
}
