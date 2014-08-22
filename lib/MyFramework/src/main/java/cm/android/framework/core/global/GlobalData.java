package cm.android.framework.core.global;

import cm.android.util.ObjectUtil;

import java.util.Map;

/**
 * 用户保存全局数据
 */
public class GlobalData {
    private Map<String, Object> mData = ObjectUtil.newHashMap();

    private GlobalData() {
    }

    private static final class Singleton {
        private static final GlobalData INSTANCE = new GlobalData();
    }

    public static GlobalData getInstance() {
        return Singleton.INSTANCE;
    }

    public void reset() {
        mData.clear();
    }

    /**
     * 获取全局数据
     *
     * @param <T>
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getData(String key) {
        return (T) mData.get(key);
    }

    /**
     * 保存全局数据
     *
     * @param <T>
     * @param key
     * @param value
     * @return
     */
    public <T> Object putData(String key, T value) {
        return mData.put(key, value);
    }
}
