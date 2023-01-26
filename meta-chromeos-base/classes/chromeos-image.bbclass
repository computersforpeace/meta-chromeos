inherit core-image

CHROMEOS_IMAGE_EXTRA_INSTALL ?= ""

create_rootfs_dirs() {
    mkdir -p \
        ${IMAGE_ROOTFS}/mnt/stateful_partition \
        ${IMAGE_ROOTFS}/mnt/empty \
        ${IMAGE_ROOTFS}/home/chronos
}
IMAGE_PREPROCESS_COMMAND += "create_rootfs_dirs;"
