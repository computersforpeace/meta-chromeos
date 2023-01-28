SUMMARY = "Power Manager for Chromium OS"
DESCRIPTION = "Power Manager for Chromium OS"
HOMEPAGE = "http://dev.chromium.org/chromium-os/packages/power_manager"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"

inherit chromeos_gn

CHROMEOS_PN = "power_manager"

S = "${WORKDIR}/src/platform2/${CHROMEOS_PN}"
B = "${WORKDIR}/build"
PR = "r4312"

CXXFLAGS:append = " -Wno-error=implicit-int-float-conversion"

GN_ARGS += 'platform_subdir="${CHROMEOS_PN}"'

# dbusxx-xml2cpp is provided by libdbus-c++-native
# protoc is provided by protbuf-native
# protoc-gen-go is provided by github.com-golang-protobuf-native
# go-generate-chromeos-dbus-bindings is provided by chromeos-dbus-bindings-native
DEPENDS:append = "\
    libbrillo \
    libchrome \
    metrics \
    shill-client \
    shill-dbus-client \
    chromeos-config-tools \
    libec \
    ml-client \
    eudev \
    system-api \
    protobuf \
    re2 \
    tpm-manager-client \
    libnl \
"

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
PACKAGECONFIG[als] = ""
PACKAGECONFIG[amd64] = ""
PACKAGECONFIG[cellular] = ""
PACKAGECONFIG[cras] = ""
PACKAGECONFIG[cros_embedded] = ""
PACKAGECONFIG[cros_host] = ""
PACKAGECONFIG[display_backlight] = ""
PACKAGECONFIG[fuzzer] = ""
PACKAGECONFIG[has_keyboard_backlight] = ""
PACKAGECONFIG[iioservice] = ""
PACKAGECONFIG[keyboard_includes_side_buttons] = ""
PACKAGECONFIG[keyboard_convertible_no_side_buttons] = ""
PACKAGECONFIG[legacy_power_button] = ""
PACKAGECONFIG[powerd_manual_eventlog_add] = ""
PACKAGECONFIG[powerknobs] = ""
PACKAGECONFIG[profiling] = ""
PACKAGECONFIG[systemd] = ""
PACKAGECONFIG[tcmalloc] = ""
PACKAGECONFIG[test] = ""
PACKAGECONFIG[touchpad_wakeup] = ""
PACKAGECONFIG[touchscreen_wakeup] = ""
PACKAGECONFIG[unibuild] = ""
PACKAGECONFIG[wilco] = ""
PACKAGECONFIG[qrtr] = ""

GN_ARGS += ' \
    use={ \
        als=${@bb.utils.contains('PACKAGECONFIG', 'als', 'true', 'false', d)} \
        amd64=${@bb.utils.contains('PACKAGECONFIG', 'als', 'true', 'false', d)} \
        cellular=${@bb.utils.contains('PACKAGECONFIG', 'cellular', 'true', 'false', d)} \
        cras=${@bb.utils.contains('PACKAGECONFIG', 'cras', 'true', 'false', d)} \
        cros_embedded=${@bb.utils.contains('PACKAGECONFIG', 'cros_embedded', 'true', 'false', d)} \
        cros_host=${@bb.utils.contains('PACKAGECONFIG', 'cros_host', 'true', 'false', d)} \
        display_backlight=${@bb.utils.contains('PACKAGECONFIG', 'display_backlight', 'true', 'false', d)} \
        fuzzer=${@bb.utils.contains('PACKAGECONFIG', 'fuzzer', 'true', 'false', d)} \
        has_keyboard_backlight=${@bb.utils.contains('PACKAGECONFIG', 'has_keyboard_backlight', 'true', 'false', d)} \
        iioservice=${@bb.utils.contains('PACKAGECONFIG', 'iioservice', 'true', 'false', d)} \
        keyboard_includes_side_buttons=${@bb.utils.contains('PACKAGECONFIG', 'keyboard_includes_side_buttons', 'true', 'false', d)} \
        keyboard_convertible_no_side_buttons=${@bb.utils.contains('PACKAGECONFIG', 'keyboard_convertible_no_side_buttons', 'true', 'false', d)} \
        legacy_power_button=${@bb.utils.contains('PACKAGECONFIG', 'legacy_power_button', 'true', 'false', d)} \
        powerd_manual_eventlog_add=${@bb.utils.contains('PACKAGECONFIG', 'powerd_manual_eventlog_add', 'true', 'false', d)} \
        powerknobs=${@bb.utils.contains('PACKAGECONFIG', 'powerknobs', 'true', 'false', d)} \
        profiling=${@bb.utils.contains('PACKAGECONFIG', 'profiling', 'true', 'false', d)} \
        systemd=${@bb.utils.contains('PACKAGECONFIG', 'systemd', 'true', 'false', d)} \
        tcmalloc=${@bb.utils.contains('PACKAGECONFIG', 'tcmalloc', 'true', 'false', d)} \
        test=${@bb.utils.contains('PACKAGECONFIG', 'test', 'true', 'false', d)} \
        touchpad_wakeup=${@bb.utils.contains('PACKAGECONFIG', 'touchpad_wakeup', 'true', 'false', d)} \
        touchscreen_wakeup=${@bb.utils.contains('PACKAGECONFIG', 'touchscreen_wakeup', 'true', 'false', d)} \
        unibuild=${@bb.utils.contains('PACKAGECONFIG', 'unibuild', 'true', 'false', d)} \
        wilco=${@bb.utils.contains('PACKAGECONFIG', 'wilco', 'true', 'false', d)} \
        qrtr=${@bb.utils.contains('PACKAGECONFIG', 'qrtr', 'true', 'false', d)} \
    } \
'

do_install() {
    install -d "${D}${bindir}"

    install -m 0755 \
        backlight_tool \
        cpufreq_config \
        dump_power_status \
        powerd \
        powerd_setuid_helper \
        power_supply_info \
        set_cellular_transmit_power \
        set_wifi_transmit_power \
        check_powerd_config \
        inject_powerd_input_event \
        powerd_dbus_suspend \
        send_debug_power_status \
        set_power_policy \
        suspend_delay_sample \
        "${D}${bindir}/"

    #scripts
    #scripts for testing and debugging
    #more

    install -d "${D}${datadir}/cros/init"
    install -m 0755 "${S}/tools/temp_logger.sh" "${D}${datadir}/cros/init/"

    install -d "${D}${sysconfdir}/dbus-1/system.d"
    install -m 0644 "${S}"/dbus/org.chromium.PowerManager.conf "${D}${sysconfdir}/dbus-1/system.d/"

    # udev scripts and rules.
    install -d "${D}/lib/udev/rules.d"
    install -m 0755 "${S}"/udev/*.sh "${D}/lib/udev/"
    install -m 0644 "${S}"/udev/*.rules "${D}/lib/udev/rules.d/"

    install -d "${D}${sysconfdir}/init"
    install -m 0644 "${S}"/init/upstart/*.conf "${D}${sysconfdir}/init/"

    install -d "${D}${datadir}/cros/init"
    install -m 0755 "${S}"/init/shared/powerd-pre-start.sh "${D}${datadir}/cros/init/"
}
FILES:${PN} += "${datadir}/cros/init"
