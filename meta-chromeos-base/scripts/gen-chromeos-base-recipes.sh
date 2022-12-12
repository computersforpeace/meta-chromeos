#!/bin/bash
rm -rf ./recipes-generated
ebuilds=$(find $1 -name *-r*.ebuild )
for ebuild in $ebuilds; do
	base=$(basename --suffix=.ebuild $ebuild )
	echo $base
	CHROMEOS_PN="$(echo $base | gawk 'match($0, /(.*)-([0-9].*)-(r[0-9]+)$/, a) {print a[1]}')"
	PN="$(echo ${CHROMEOS_PN}  | tr '_' '-')"
	PV="$(echo $base | gawk 'match($0, /(.*)-([0-9].*)-(r[0-9]+)$/, a) {print a[2]}')"
	PR="$(echo $base | gawk 'match($0, /(.*)-([0-9].*)-(r[0-9]+)$/, a) {print a[3]}')"
	DESCRIPTION="$(grep DESCRIPTION $ebuild | gawk 'match($0, /DESCRIPTION="(.*)"$/, h) {print h[1]}')"
	HOMEPAGE="$(grep HOMEPAGE $ebuild | gawk 'match($0, /HOMEPAGE="(.*)"$/, h) {print h[1]}')"
	LICENSE="$(grep LICENSE $ebuild | gawk 'match($0, /LICENSE="(.*)"$/, h) {print h[1]}')"
	mkdir -p ./recipes-generated/${PN}
	RECIPE="./recipes-generated/${PN}/${PN}_${PV}.bb"
	touch $RECIPE
	{
	printf "SUMMARY = \"${DESCRIPTION}\"\nDESCRIPTION = \"${DESCRIPTION}\"\n"
	if [ -n ${HOMEPAGE} ]; then
	    echo "HOMEPAGE = \"${HOMEPAGE}\""
	fi
	case $LICENSE in
		BSD-Google)
			echo 'LICENSE = "BSD-3-Clause"\nLIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"'
			;;
		GPL-2)
			echo 'LICENSE = "GPL-2.0"'
			;;
		*)
			echo "LICENSE = \"${LICENSE}\""
			;;
	esac
	if [ -n "${HOMEPAGE}" ]; then
		platform2="$(echo ${HOMEPAGE} | grep platform2 )"
		platform="$(echo ${HOMEPAGE} | grep \/chromiumos\/platform\/ )"
		printf "\ninherit chromeos_gn\n"
		if [ -n "${platform2}" ]; then
			if [ "${CHROMEOS_PN}" = "${PN}" ]; then
				printf '\nS = "${WORKDIR}/src/platform2/${BPN}"\n'
			else
				printf "\nCHROMEOS_PN = \"${CHROMEOS_PN}\"\n"
				printf '\nS = "${WORKDIR}/src/platform2/${CHROMEOS_PN}"\n'
			fi
		        echo 'B = "${WORKDIR}/build"'
		elif [ -n "${platform}" ]; then
			base_platform="$(grep HOMEPAGE $ebuild | gawk 'match($0, /platform\/([a-z0-9_-]*)/, h) {print h[1]}')"
			echo "DEBUG:  base_platform=${base_platform}"
			if [ "${CHROMEOS_PN}" != "${base_platform}" -a "${base_platform}" != "" ]; then
				echo "require recipes-chromeos/chromiumos-platform/chromiumos-platform-${base_platform}.inc"
				printf "\nS = \"${WORKDIR}/src/platform/${base_platform}\"\n"
			elif [ "${CHROMEOS_PN}" = "${PN}" ]; then
				echo 'require recipes-chromeos/chromiumos-platform/chromiumos-platform-${BPN}.inc'
				printf '\nS = "${WORKDIR}/src/platform/${BPN}"\n'
			else
				printf "\nCHROMEOS_PN = \"${CHROMEOS_PN}\"\n"
				echo 'require recipes-chromeos/chromiumos-platform/chromiumos-platform-${CHROMEOS_PN}.inc'
				printf '\nS = "${WORKDIR}/src/platform/${CHROMEOS_PN}"\n'
			fi
			echo 'B = "${WORKDIR}/build"'
		fi
	fi
	printf "PR = \"${PR}\"\n\n"

	if [ -n "${platform2}" ]; then
		if [ "${CHROMEOS_PN}" = "${PN}" ]; then
			printf "GN_ARGS += 'platform_subdir=\"\${BPN}\"'\n\n"
		else
			printf "GN_ARGS += 'platform_subdir=\"\${CHROMEOS_PN}\"'\n\n"
		fi
	elif [ -n "${platform}" ]; then
		if [ "${CHROMEOS_PN}" = "${PN}" ]; then
			printf "GN_ARGS += 'platform_subdir=\"../platform/\${BPN}\"'\n\n"
		else
			printf "GN_ARGS += 'platform_subdir=\"../platform/\${CHROMEOS_PN}\"'\n\n"
		fi
	fi

	IUSE="$(grep IUSE $ebuild | gawk 'match($0, /IUSE="(.*)"$/, h) {print h[1]}' | head -1 | tr -d '+' | tr -d '-')"
	if [ "${IUSE}" != "" ]; then
		printf "PACKAGECONFIG ??= \"\"\n\n"
		echo "# Description of all the possible PACKAGECONFIG fields (comma delimited):"
		echo "# 1. Extra arguments that should be added to the configure script argument list (EXTRA_OECONF or PACKAGECONFIG_CONFARGS) if the feature is enabled."
		echo "# 2. Extra arguments that should be added to EXTRA_OECONF or PACKAGECONFIG_CONFARGS if the feature is disabled."
		echo "# 3. Additional build dependencies (DEPENDS) that should be added if the feature is enabled."
		echo "# 4. Additional runtime dependencies (RDEPENDS) that should be added if the feature is enabled."
		echo "# 5. Additional runtime recommendations (RRECOMMENDS) that should be added if the feature is enabled."
		echo "# 6. Any conflicting (that is, mutually exclusive) PACKAGECONFIG settings for this feature.\n"

		echo "# Empty PACKAGECONFIG options listed here to avoid warnings."
		echo "# The .bb file should use these to conditionally add patches,"
		echo "# command-line switches and dependencies."

		for use in $IUSE; do
			echo "PACKAGECONFIG[${use}] = \"\""
		done
		echo
		echo "GN_ARGS += ' \\"
		echo "    use={ \\"
		for use in $IUSE; do
			echo "        ${use}=\${@bb.utils.contains('PACKAGECONFIG', '${use}', 'true', 'false', d)} \\"
		done
		echo "    } \\"
		echo "'"
	fi		

	printf "\ndo_compile() {\n"
	if [ "${CHROMEOS_PN}" = "${PN}" ]; then
		echo "    ninja -C \${B} \${BPN}"
	else
		echo "    ninja -C \${B} \${CHROMEOS_PN}"
	fi
	printf "}\n\n"

	echo "do_install() {"
	echo "    :"
	echo "}"
	} > "${RECIPE}"
done
