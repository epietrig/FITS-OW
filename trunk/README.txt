Setting up JEP (Linux)

Set environment variable LD_PRELOAD with the value returned by the following command:

ldd src/jep/.libs/libjep.so | grep libpython | awk '{$3}'
