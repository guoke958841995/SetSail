package com.sxhalo.PullCoal.bean;



/**
 * Created by amoldZhang on 2017/7/18.
 */
public class HttpResult<T> {


    /**
     * head : {"msg":"success","cmd":"/lamapi/getHomeData","param":"null"}
     * body : {"title":"首页数据","data":[{"title":"轮播图","data":[{"pushtime":"03-07 17:49","title":"\u201c互联网+煤炭\u201d，让煤炭交易一步到位","source":"陕西光环网络科技有限公司","imageUrl":"http://login.sxhalo.com:80/lamadm/upload/1488880033289.png","clickNum":"435","articleId":"41","publishedUrl":"articledetail/41.htm"},{"pushtime":"03-07 17:52","title":"一手货源，更多财源，尽在拉煤宝app","source":"陕西光环网络科技有限公司","imageUrl":"http://login.sxhalo.com:80/lamadm/upload/1488880217008.png","clickNum":"539","articleId":"42","publishedUrl":"articledetail/42.htm"}]},{"title":"功能列表","data":[{"imageUrl":"http://172.16.99.116:8080/lamapi/upload/3.png","functionId":"3","functionName":"我要买煤"},{"imageUrl":"http://172.16.99.116:8080/lamapi/upload/7.png","functionId":"7","functionName":"查找信息部"}]},{"title":"头条资讯","data":[{"pushtime":"03-07 17:49","title":"\u201c互联网+煤炭\u201d，让煤炭交易一步到位","source":"陕西光环网络科技有限公司","imageUrl":"http://login.sxhalo.com:80/lamadm/upload/1488880033289.png","clickNum":"435","articleId":"41","publishedUrl":"articledetail/41.htm"},{"pushtime":"03-07 17:52","title":"一手货源，更多财源，尽在拉煤宝app","source":"陕西光环网络科技有限公司","imageUrl":"http://login.sxhalo.com:80/lamadm/upload/1488880217008.png","clickNum":"539","articleId":"42","publishedUrl":"articledetail/42.htm"}]},{"title":"推荐的信息部","data":[{"creditRating":"3","contactPhone":"18691253363","address":"301省道","imageUrl":"http://login.sxhalo.com:80/sxhaloadm/upload/1497247982609.jpg","coalSalesId":"20000014","companyName":"鸿光物流15号","contactPerson":"张金莲"},{"creditRating":"3","contactPhone":"15596569769","address":"大昌汗乡","imageUrl":"http://login.sxhalo.com:80/sxhaloadm/upload/1497147592359.jpg","coalSalesId":"20000010","companyName":"168信息部","contactPerson":"张英根"}]},{"title":"明星司机","data":[{"carLong":"","vehicleState":"","imageUrl":"","regionName":"","state":"null","totalSingular":"","creditRating":"3","vehicleModels":"","phoneNumber":"13466832278","friendState":"","userId":"80000242","name":"","carNumber":"","driverYear":"","whetherLine":"","carLoad":""},{"carLong":"","vehicleState":"","imageUrl":"","regionName":"","state":"null","totalSingular":"","creditRating":"3","vehicleModels":"","phoneNumber":"15832062302","friendState":"","userId":"80000236","name":"","carNumber":"","driverYear":"","whetherLine":"","carLoad":""}]}]}
     */

    private HeadBean head;
    private T body;

    public HeadBean getHead() {
        return head;
    }

    public void setHead(HeadBean head) {
        this.head = head;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public static class HeadBean {
        /**
         * msg : success/error
         * cmd : /lamapi/getHomeData
         * param : {}
         * error_code : -1
         * error : ""
         */

        private String msg;
        private String cmd;
        private Object param;
        private String error_code;
        private String error;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        public Object getParam() {
            return param;
        }

        public void setParam(String param) {
            this.param = param;
        }

        public String getError_code() {
            return error_code;
        }

        public void setError_code(String error_code) {
            this.error_code = error_code;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        @Override
        public String toString() {
            return "HeadBean{" +
                    "msg='" + msg + '\'' +
                    ", cmd='" + cmd + '\'' +
                    ", param=" + param +
                    ", error_code='" + error_code + '\'' +
                    ", error='" + error + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "head=" + head +
                ", body=" + body +
                '}';
    }
}
