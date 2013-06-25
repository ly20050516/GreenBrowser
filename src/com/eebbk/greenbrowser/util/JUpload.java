package com.eebbk.greenbrowser.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.eebbk.greenbrowser.model.BaseObject;

/**
 * 
 * 说明：上传数据<br>
 * 公司名称 ：步步高教育电子<br>
 * 
 * @author 李修金
 * @version 1.0
 */
@SuppressWarnings("unused")
public class JUpload {

	/**
	 * 
	 * 说明：异步上传方法<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	public class AsyncUploader extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return httpPostMethod(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
		}
	}

	private static final String DOMAIN = "http://h600s.eebbk.net/";

	/** 下载 */
	private static final String downloaderUrl = DOMAIN
			+ "giAgreeWeb/getListByDate";

	/** timeoutConnection http链接时间 */
	private static final int timeoutConnection = 180000;

	/** timeoutSocket socket保持链接时间 */
	private static final int timeoutSocket = 180000;

	/** 上传URI地址 */
	private static final String uploaderUrl = DOMAIN
			+ "greenInternet/commitUpInfo";

	/**
	 * 转换数据流
	 * 
	 * @param is
	 * @return String
	 */
	private static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	public static String doPost(String url, List<NameValuePair> params) {
		HttpPost httpPostRequest = new HttpPost(url);
		try {
			if (params != null) {
				HttpEntity httpEntity = new UrlEncodedFormEntity(params,
						"UTF-8");
				httpPostRequest.setEntity(httpEntity);
			}

			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient httpClient = new DefaultHttpClient(httpParameters);

			HttpResponse httpResponse = httpClient.execute(httpPostRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				if (httpResponse.getEntity() != null) {
					InputStream inputStream = httpResponse.getEntity()
							.getContent();
					return convertStreamToString(inputStream);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static List<BaseObject> getBaseObjects(String josnText) {
		if (TextUtils.isEmpty(josnText)) {
			return null;
		}

		ArrayList<BaseObject> mBaseObjects = new ArrayList<BaseObject>();
		BaseObject mBaseObject;
		try {
			JSONObject jo = new JSONObject(josnText);
			JSONArray jsonArray = (JSONArray) jo.get("AGREEDWEBSITE");
			for (int i = 0; i < jsonArray.length(); ++i) {
				JSONObject o = (JSONObject) jsonArray.get(i);
				mBaseObject = new BaseObject();
				mBaseObject.setCatalog(o.getString("catalog"));
				mBaseObject.setCatalogsn(o.getString("catalogsn"));
				mBaseObject.setName(o.getString("name"));
				mBaseObject.setUrl(o.getString("uri"));
				mBaseObject.setLogo(o.getString("logo"));
				mBaseObject.setVisits(o.getInt("visit"));
				mBaseObject.setIsme(o.getInt("isme"));
				mBaseObjects.add(mBaseObject);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return mBaseObjects;
	}

	/**
	 * POST方法
	 * 
	 * @param content
	 * @return String
	 */
	public static String httpPostMethod(String content) {
		HttpPost httpPost = new HttpPost(uploaderUrl);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("machineId", "123456121"));
		params.add(new BasicNameValuePair("userName", "rainzc"));
		params.add(new BasicNameValuePair("content", content));

		try {
			HttpEntity httpEntity = new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(httpEntity);
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 获取的数据流转化为Json格式
				InputStream inputStream = httpResponse.getEntity().getContent();
				String json = convertStreamToString(inputStream);

				if (json.contains("gi_insert_success_200")) {
					// 上传成功
					return "上传成功";

				} else if (json.contains("gi_insert_fail_404")) {
					// 上传失败
					return "上传失败";
				}
			} else {
				return "请求失败";
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "上传失败";
	}

	private Context mContext;

	public JUpload(Context context) {
		super();
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	public List<BaseObject> downloader(String maxDate) {

		return null;
		// List<NameValuePair> params = new ArrayList<NameValuePair>();
		// if (TextUtils.isEmpty(maxDate)) {
		// params.add(new BasicNameValuePair("updateDate", maxDate));
		// }
		// params.add(new BasicNameValuePair("suitablecrowd", "1"));
		// params.add(new BasicNameValuePair("TPSecNotice&TPNotCheck", null));
		// return getBaseObjects(doPost(downloaderUrl, params));
	}

	/**
	 * 上传方法
	 * 
	 * @param contentString
	 * 
	 */
	public void uploader(String contentString) {
		if (JUtil.isNetworkAvailable(mContext)) {
			new AsyncUploader().execute(contentString);
		} else {
			JUtil.startSetWifiEnabled(mContext);
		}
	}
}
