SUMMARY = "Shill Connection Manager for Chromium OS"
DESCRIPTION = "Shill Connection Manager for Chromium OS"
HOMEPAGE = "https://chromium.googlesource.com/chromiumos/platform2/+/HEAD/shill/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"

inherit chromeos_gn platform

CHROMEOS_PN = "${BPN}"

S = "${WORKDIR}/src/platform2/${CHROMEOS_PN}"
B = "${WORKDIR}/build"
PR = "r3407"

# There are a few warnings the Yocto toolchain is catching in metrics code. The
# metrics recipe has some patches, but we don't.
CXXFLAGS:append = " -Wno-error=implicit-int-float-conversion"

DEPENDS:append = "\
    chromeos-dbus-bindings-native \
    dbus \
    libbrillo \
    libchrome \
    re2 \
    system-api \
    protobuf \
    protobuf-native \
    util-linux \
    bootstat \
    chaps \
    libpasswordprovider \
    metrics \
    nsswitch \
    patchpanel-client \
    shill-client \
    shill-net \
    c-ares \
    libtirpc \
    rootdev \
    c-ares \
    power-manager-client \
"

RDEPENDS:${PN} += "\
    libminijail \
    minijail \
    wpa-supplicant \
"

GN_ARGS += 'platform_subdir="${CHROMEOS_PN}"'

PACKAGECONFIG ??= "wifi"

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
PACKAGECONFIG[cellular] = ""
PACKAGECONFIG[cros_host] = ""
PACKAGECONFIG[fuzzer] = ""
PACKAGECONFIG[metrics_uploader] = ""
PACKAGECONFIG[passive_metrics] = ""
PACKAGECONFIG[profiling] = ""
PACKAGECONFIG[sae_h2e] = ""
PACKAGECONFIG[systemd] = ""
PACKAGECONFIG[tcmalloc] = ""
PACKAGECONFIG[test] = ""
PACKAGECONFIG[tpm] = ""
PACKAGECONFIG[vpn] = ""
PACKAGECONFIG[wake_on_wifi] = ""
PACKAGECONFIG[wifi] = ""
PACKAGECONFIG[wired_8021x] = ""
PACKAGECONFIG[wpa3_sae] = ""
PACKAGECONFIG[wireguard] = ""

GN_ARGS += ' \
    use={ \
        cellular=${@bb.utils.contains('PACKAGECONFIG', 'cellular', 'true', 'false', d)} \
        cros_host=${@bb.utils.contains('PACKAGECONFIG', 'cros_host', 'true', 'false', d)} \
        fuzzer=${@bb.utils.contains('PACKAGECONFIG', 'fuzzer', 'true', 'false', d)} \
        metrics_uploader=${@bb.utils.contains('PACKAGECONFIG', 'metrics_uploader', 'true', 'false', d)} \
        passive_metrics=${@bb.utils.contains('PACKAGECONFIG', 'passive_metrics', 'true', 'false', d)} \
        profiling=${@bb.utils.contains('PACKAGECONFIG', 'profiling', 'true', 'false', d)} \
        tcmalloc=${@bb.utils.contains('PACKAGECONFIG', 'tcmalloc', 'true', 'false', d)} \
        sae_h2e=${@bb.utils.contains('PACKAGECONFIG', 'sae_h2e', 'true', 'false', d)} \
        systemd=${@bb.utils.contains('PACKAGECONFIG', 'systemd', 'true', 'false', d)} \
        tcmalloc=${@bb.utils.contains('PACKAGECONFIG', 'tcmalloc', 'true', 'false', d)} \
        test=${@bb.utils.contains('PACKAGECONFIG', 'test', 'true', 'false', d)} \
        tpm=${@bb.utils.contains('PACKAGECONFIG', 'tpm', 'true', 'false', d)} \
        vpn=${@bb.utils.contains('PACKAGECONFIG', 'vpn', 'true', 'false', d)} \
        wake_on_wifi=${@bb.utils.contains('PACKAGECONFIG', 'wake_on_wifi', 'true', 'false', d)} \
        wifi=${@bb.utils.contains('PACKAGECONFIG', 'wifi', 'true', 'false', d)} \
        wired_8021x=${@bb.utils.contains('PACKAGECONFIG', 'wired_8021x', 'true', 'false', d)} \
        wpa3_sae=${@bb.utils.contains('PACKAGECONFIG', 'wpa3_sae', 'true', 'false', d)} \
        wireguard=${@bb.utils.contains('PACKAGECONFIG', 'wireguard', 'true', 'false', d)} \
    } \
'

python do_install:append() {
    bb.build.exec_func('do_install_shill', d)
}

do_install_shill() {
    install -d "${D}${bindir}"
    install -d "${D}${sbindir}"

    install -m 0755 "${S}"/bin/set_wifi_regulatory "${D}${sbindir}"

    for e in \
        "${S}"/bin/set_arpgw \
        "${S}"/bin/set_wake_on_lan \
        "${S}"/bin/shill_login_user \
        "${S}"/bin/shill_logout_user \
        "${S}"/bin/wpa_debug \
        shill \
        ; do
        install -m 0755 "${e}" "${D}${bindir}/"
    done

    install -d "${D}${sysconfdir}"
    ln -sf /run/shill/resolv.conf "${D}${sysconfdir}/resolv.conf"
    # TODO: lots more, supplicant, etc.
}
