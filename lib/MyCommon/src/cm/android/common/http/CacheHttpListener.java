package cm.android.common.http;

import cm.android.common.cache.disk.entry.HttpCacheEntry;
import cm.android.util.Utils;
import org.apache.http.Header;

import java.util.Map;

public abstract class CacheHttpListener<T> extends MyJsonHttpListener<T> {

	/**
	 * 
	 */
	public CacheHttpListener() {
		super();
	}

	public void setCache(String url) {
		cacheHolder.setCache(url);
	}

	private CacheHolder cacheHolder = new CacheHolder();

	@Override
	final void onSuccess(int statusCode, Header[] headers,
			byte[] responseBytes, T responseMap) {
		// Map<String, Object> responseMap = JSON.parseObject(
		// responseBody.getBytes(), Map.class);

		Map<String, String> headMap = Utils.genHeaderMap(headers);

		// 保存缓存
		cacheHolder.saveCache(headMap, responseBytes);
		// 返回数据
		onSuccess(statusCode, headMap, responseMap);
	}

	private static class CacheHolder {
		private HttpCacheEntry entry = null;

		private void saveCache(Map<String, String> headers, byte[] responseBytes) {
			if (entry != null) {
				entry.setHeaders(headers);
				String content = new String(responseBytes);
				entry.setContent(content);
				HttpLoader.saveCache(entry.getUri(), entry);
			}
		}

		public void setCache(String url) {
			entry = new HttpCacheEntry();
			entry.setUri(url);
		}
	}
}
