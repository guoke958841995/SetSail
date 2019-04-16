package com.sxhalo.PullCoal.retrofithttp.api;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * retrofit注解：
 *
 * 方法注解，包含@GET、@POST、@PUT、@DELETE、@PATH、@HEAD、@OPTIONS、@HTTP。
 * 标记注解，包含@FormUrlEncoded、@Multipart、@Streaming。
 * 参数注解，包含@Query,@QueryMap、@Body、@Field，@FieldMap、@Part，@PartMap。
 * 其他注解，@Path、@Header,@Headers、@Url
 *
 * Created by amoldZhang on 2017/7/12.
 */
public interface MovieService {

//    /**
//     * 自定义 GET 连接
//     * @param params
//     * @return
//     */
//    @GET("{url}")
//    Observable<HttpResult> executeGet(
//            @Path(value = "url") String url,
//            @QueryMap Map<String, String> params);

    /**
     * 自定义 GET 连接
     * @param params
     * @return
     */
    @GET
    Observable<HttpResult> executeGet(
            @Url String url,
            @QueryMap Map<String, String> params);

    /**
     * 自定义 POST 连接
     * @param url
     * @param maps
     * @return
     */
    @POST
    Observable<HttpResult> executePost(
            @Url String url,
            @QueryMap Map<String, String> maps);

    /**
     * 单文件/图片 上传
     * @param body
     * @param params
     * @return
     */
    @Multipart
    @POST
    Observable<HttpResult> upLoadFile(
            @Url String url,
            @Part("file\"; filename=\"lameibao_image.png") RequestBody body,
            @QueryMap Map<String, String> params);

    /**
     * 文件加参数上传 多文件
     * @param files  文件数据流
     * @param params 要出入的参数
     * @return
     */
    @POST
    Observable<HttpResult> getFellAddParams(
            @Url String url,
            @Body MultipartBody files,
            @QueryMap Map<String, String> params);

    /**
     * 多文件/图片 上传
     * @param url
     * @param headers
     * @param description
     * @param maps
     * @return
     */
    @POST
    Observable<HttpResult> uploadFiles(
            @Url String url,
            @Path("headers") Map<String, String> headers,
            @Part("filename") String description,
            @PartMap() Map<String, RequestBody> maps);

    /**
     * 文件下载
     * @param fileUrl
     * @return
     */
    @Streaming
    @GET
    Observable<HttpResult> downloadFile(@Url String fileUrl);

}
