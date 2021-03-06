#!/bin/bash
. ffmpeg-x264_build_settings.sh

export PLATFORM="android-21"
SYSROOT=$ANDROID_NDK/platforms/$PLATFORM/arch-arm64
TOOLCHAIN=$ANDROID_NDK/toolchains/aarch64-linux-android-4.9/prebuilt/linux-x86_64
rm -f $(pwd)/compat/strtod.o

function build_target
{
./configure \
  $COMMON $CONFIGURATION \
  --prefix=$PREFIX \
  --cross-prefix=$CROSS_PREFIX \
  --sysroot=$SYSROOT \
  --nm=${CROSS_PREFIX}nm \
  --cc=${CROSS_PREFIX}gcc \
  --extra-libs="-lgcc" \
  --target-os=linux \
  --disable-asm \
  --arch=arm64 \
  --cpu=cortex-a57 \
  --extra-cflags="-O3 -DANDROID -Dipv6mr_interface=ipv6mr_ifindex -march=armv8-a -fPIC -fasm -Wno-psabi -fno-short-enums -fno-strict-aliasing -I../android/$CPU/include/x264" \
  --extra-ldflags="-lc -lm -ldl -llog $ADDI_LDFLAGS -L../android/$CPU/lib"

make clean
make -j4
make install
}

export CPU=arm64-v8a
PREFIX=../android/$CPU 
CROSS_PREFIX=$TOOLCHAIN/bin/aarch64-linux-android-

pushd ffmpeg
build_target
popd
echo "=== Android ffmpeg for $CPU builds completed ==="
