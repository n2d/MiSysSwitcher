#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <Log.h>




int SwitchSystem(JNIEnv * env, jobject obj,int num)
{
	const char *sys0="boot-system0";
	const char *sys1="boot-system1";
	if(num<0 || num>1)
		return -1;

	FILE *fp=fopen("/dev/block/mmcblk0p11","r+");
	if(fp==NULL)
		return -2;
	fseek(fp,0x1000,0);
	if(num==0)
		fwrite(sys0,1,strlen(sys0),fp);
	else
		fwrite(sys1,1,strlen(sys1),fp);
	fclose(fp);
	return 0;
}

//################################################################################

static const char *classPathName = "www/cnsys/org/MiuiSwitcher/JNIMain";

// JNI函数调用列表
static JNINativeMethod methods[] = {
        {"SwitchSystem", "(I)I", (void*)SwitchSystem },

};
//##################################################################################



//====================================================================
/*
 * Register several native methods for one class.
 */
static int registerNativeMethods(JNIEnv* env, const char* className,
                JNINativeMethod* gMethods, int numMethods)
{
    jclass clazz;
    fprintf(stderr, "RegisterNatives start for '%s'", className);
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        fprintf(stderr, "Native registration unable to find class '%s'", className);
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        fprintf(stderr, "RegisterNatives failed for '%s'", className);
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

/*
 * Register native methods for all classes we know about.
 *
 * returns JNI_TRUE on success.
 */
static int registerNatives(JNIEnv* env)
{
    if (!registerNativeMethods(env, classPathName,
        methods, sizeof(methods) / sizeof(methods[0]))) {
            return JNI_FALSE;
    }

    return JNI_TRUE;
}


// ----------------------------------------------------------------------------


/*
 * This is called by the VM when the shared library is first loaded.
 */

typedef union {
    JNIEnv* env;
    void* venv;
} UnionJNIEnvToVoid;

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    UnionJNIEnvToVoid uenv;
    uenv.venv = NULL;
    jint result = -1;
    JNIEnv* env = NULL;

    printf("JNI_OnLoad");

    if (vm->GetEnv(&uenv.venv, JNI_VERSION_1_4) != JNI_OK) {
        fprintf(stderr, "GetEnv failed");
        goto bail;
    }

    env = uenv.env;

    if (registerNatives(env) != JNI_TRUE) {
        fprintf(stderr, "GetEnv failed");
        goto bail;
}

        result = JNI_VERSION_1_4;
bail:
    return result;
}


