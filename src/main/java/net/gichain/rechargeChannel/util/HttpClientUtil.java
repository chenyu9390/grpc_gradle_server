/*
 * Project Name: backstage
 * File Name: HttpClientUtil.java
 * Class Name: HttpClientUtil
 *
 * Copyright 2014 Hengtian Software Inc
 *
 * Licensed under the Hengtiansoft
 *
 * http://www.hengtiansoft.com
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.gichain.rechargeChannel.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
* @author qiaozhenhan
* Date Apr 26, 2018
*/
@Slf4j
public class HttpClientUtil {   /**
     * 向指定URL发送GET方法的请求
     *
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param){
        String result = "";
        BufferedReader in = null;
        String urlNameString = "";
        InputStreamReader inputStreamReader = null;
        URLConnection connection = null;
        try {
            if(StringValueUtil.isEmpty(param)){
                urlNameString = url;
            }else {
                urlNameString = url + "?" + param;
            }
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            inputStreamReader = new InputStreamReader(connection.getInputStream());
            in = new BufferedReader(inputStreamReader);
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        }catch (Exception e){
            log.error(StringValueUtil.appendString(url,"接口请求失败"),e);
        }
        // 使用finally块来关闭输入流
        finally {
            if(inputStreamReader != null){
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) throws IOException {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        InputStreamReader inputStreamReader = null;
        try{
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            inputStreamReader = new InputStreamReader(conn.getInputStream());
            in = new BufferedReader(inputStreamReader);
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        }finally {
            try {
                if(in != null){
                    in.close();
                }
            }catch (Exception e){
                log.error("接口请求失败：",e);
            }
            try {
                if(out != null){
                    out.close();
                }
            }catch (Exception e){
                log.error("接口请求失败：",e);
            }

        }
        return result;
    } 
    
	public static JSONObject getRestful(String url, String way) {
		String output;

		try {
			URL restServiceURL = new URL(url);
			HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
			httpConnection.setConnectTimeout(30000);
			httpConnection.setReadTimeout(30000);
			httpConnection.setRequestMethod(way); // way: POST GET
			httpConnection.setRequestProperty("Accept", "application/json");
			if (httpConnection.getResponseCode() != 200) {
				System.out.println("错误码是 ：：：  " + httpConnection.getResponseCode());
			}
			BufferedReader responseBuffer = new BufferedReader(new InputStreamReader((httpConnection.getInputStream())));

			JSONObject temp = new JSONObject();
			while ((output = responseBuffer.readLine()) != null) {
				temp = JSONObject.parseObject(output);
			}
			httpConnection.disconnect();

			return temp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
