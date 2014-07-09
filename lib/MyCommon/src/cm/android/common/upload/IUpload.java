package cm.android.common.upload;

import cm.android.common.upload.db.UploadBean;

import java.util.Map;

public interface IUpload {
	/**
	 * 请求上传url地址
	 */
	public Map<String, String> getUploadURL(UploadBean uploadBean);

	/**
	 * 获取上传文件位置
	 */
	public Map<String, String> getLastUploadPos(UploadBean uploadBean);

	/**
	 * 上传文件
	 */
	public void fileUpload();

	/**
	 * 完成上传任务是所有任务完成还是单个文件上传
	 */
	public void onUploadComplete(UploadBean uploadBean);
}