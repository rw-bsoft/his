
package phis.application.yb.source;

/**
 * doel创建管理类 
 * @author Administrator
 *
 */
 class ModelManager {
    public static final String TAG = ModelManager.class.getSimpleName();



    /**
     * 根据枚举类型动态创建model实现类
     * @param type
     * @return
     */
    public static MedicareModel  createVpnProfile(YbModelType type) {
        return createVpnProfile(type, false);
    }


    public static MedicareModel   createVpnProfile(YbModelType type, boolean customized) {
        try {
        	MedicareModel p = (MedicareModel) type.getmClass().newInstance();
            return p;
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

   
}
