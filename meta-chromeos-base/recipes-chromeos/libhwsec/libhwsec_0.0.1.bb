SUMMARY = "Crypto and utility functions used in TPM related daemons."
DESCRIPTION = "Crypto and utility functions used in TPM related daemons."
HOMEPAGE = "https://chromium.googlesource.com/chromiumos/platform2/+/HEAD/libhwsec/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"

inherit chromeos_gn python3native

S = "${WORKDIR}/src/platform2/${BPN}"
B = "${WORKDIR}/build"
PR = "r430"

DEPENDS:append = " abseil-cpp flatbuffers libbrillo libchrome libtpm-manager-client openssl protobuf system-api trunks"

# Need native flatc.
do_compile[depends] += "flatbuffers-native:do_populate_sysroot"
do_compile[depends] += "python3-jinja2-native:do_populate_sysroot"

GN_ARGS += 'platform_subdir="${BPN}"'

PACKAGECONFIG ??= "${@bb.utils.filter('MACHINE_FEATURES', 'tpm tpm2', d)}"

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
PACKAGECONFIG[fuzzer] = ",,trousers trunks,,,"
PACKAGECONFIG[profiling] = ""
PACKAGECONFIG[tcmalloc] = ""
PACKAGECONFIG[test] = ",,trunks tpm2-simulator,,,"
PACKAGECONFIG[tpm] = ",,trousers,trousers,,"
PACKAGECONFIG[tpm2] = ",,trunks,trunks,,"
PACKAGECONFIG[tpm_dynamic] = ""

GN_ARGS += ' \
    use={ \
        cros_host=${@bb.utils.contains('PACKAGECONFIG', 'cros_host', 'true', 'false', d)} \
        fuzzer=${@bb.utils.contains('PACKAGECONFIG', 'fuzzer', 'true', 'false', d)} \
        profiling=${@bb.utils.contains('PACKAGECONFIG', 'profiling', 'true', 'false', d)} \
        tcmalloc=${@bb.utils.contains('PACKAGECONFIG', 'tcmalloc', 'true', 'false', d)} \
        test=${@bb.utils.contains('PACKAGECONFIG', 'test', 'true', 'false', d)} \
        tpm=${@bb.utils.contains('PACKAGECONFIG', 'tpm', 'true', 'false', d)} \
        tpm2=${@bb.utils.contains('PACKAGECONFIG', 'tpm2', 'true', 'false', d)} \
        tpm_dynamic=${@bb.utils.contains('PACKAGECONFIG', 'tpm_dynamic', 'true', 'false', d)} \
    } \
'

do_compile() {
    ninja -C ${B} libhwsec:all
}

do_install() {
    :
}

