package com.sxhalo.PullCoal.db.dao;

import android.database.Cursor;
import android.util.Log;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.MineProduct;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by amoldZhang on 2017/1/12.
 */
public class MineProductDao {


    private double latitude;
    private double longitude;
    private Double distance;

    public MineProductDao(double latitude, double longitude, Double aDouble) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = aDouble;
    }

    public  List<MineProduct> getNearestDistance(){
        List<MineProduct> result = new ArrayList<MineProduct>();
        try {
            final double leftLat = latitude + Double.valueOf(new DecimalFormat("#.0000000").format(distance/111));
            final double leftLon = longitude + Double.valueOf(new DecimalFormat("#.0000000").format(distance/111));
            final double rightLat = latitude - Double.valueOf(new DecimalFormat("#.0000000").format(distance/111));
            final double rightLon = longitude - Double.valueOf(new DecimalFormat("#.0000000").format(distance/111));

            //			String sql = "select * from "+tableName+
            //					" where "+latitude+" > "+lat+"-1 " +
            //					"and "+latitude+" < "+lat+"+1 " +
            //					"and "+longitude+" > "+lon+"-1 " +
            //					"and "+longitude+" < "+ lon +" +1 " +
            //					"order by ACOS(SIN(("+lat+" * 3.1415) / 180 ) *SIN(("+latitude+" * 3.1415) / 180 ) " +
            //					"+COS(("+lat+" * 3.1415) / 180 ) * COS(("+latitude+" * 3.1415) / 180 ) *COS(("+lon+"* 3.1415) / 180 - ("+longitude+" * 3.1415) / 180 ) ) * 6380 " +
            //					"asc limit 10";
            //

//            			String sql = "select * from "+ "mine_product_model"+
//            					" where mine_mouth_latitude < "+ leftLat +
//            					"and mine_mouth_latitude > "+ rightLat +
//            					"and mine_mouth_longitude > "+ rightLon +
//            					"and mine_mouth_longitude < "+ leftLon +
//            					"order by ACOS(SIN(("+rightLat+" * 3.1415) / 180 ) *SIN(("+latitude+" * 3.1415) / 180 ) " +
//            					"+COS(("+lat+" * 3.1415) / 180 ) * COS(("+latitude+" * 3.1415) / 180 ) *COS(("+lon+"* 3.1415) / 180 - ("+longitude+" * 3.1415) / 180 ) ) * 6380 " +
//            					"asc limit 10";

            String sql = "select * from " + "mine_product_model" +
                    " where mine_mouth_latitude < "+ leftLat+
                    " and mine_mouth_latitude > "+ rightLat+
                    " and mine_mouth_longitude < "+leftLon+
                    " and mine_mouth_longitude > "+ rightLon  ;
            //					+
            //					" order by "+ buildDistanceQuery(latitude,longitude) +" * 6380 asc limit 10";

//            @Column(value = "mine_mouth_id")
//            private String mineMouthId;
//            @Column(value = "mine_mouth_adress")
//            private String adress;
//            @Column(value = "mine_mouth_name")
//            private String name;
//            @Column(value = "mine_mouth_longitude")
//            private String longitude;
//            @Column(value = "mine_mouth_latitude")
//            private String latitude;
//            @Column(value = "mine_mouth_type")
//            private String type;

            Cursor cursor = OrmLiteDBUtil.liteOrm.getReadableDatabase().rawQuery(sql, null);
            List<MineProduct> res = new ArrayList<MineProduct>();
            while (cursor.moveToNext()) {
                MineProduct entity = new MineProduct();
                entity.setMineMouthId(cursor.getString(cursor.getColumnIndex("mine_mouth_id")));
                entity.setLatitude(cursor.getString(cursor.getColumnIndex("mine_mouth_latitude")));
                entity.setLongitude(cursor.getString(cursor.getColumnIndex("mine_mouth_longitude")));
                entity.setName(cursor.getString(cursor.getColumnIndex("mine_mouth_name")));
                entity.setAdress(cursor.getString(cursor.getColumnIndex("mine_mouth_adress")));
                entity.setType(cursor.getString(cursor.getColumnIndex("mine_mouth_type")));
                res.add(entity);
            }

            LatLonPoint startPoint = new LatLonPoint(latitude,longitude);
            List<Double> dei = new ArrayList<Double>();
            List<MineProduct> list = new ArrayList<MineProduct>();
            int k = 0;
            for(int i = 0;i<res.size();i++){
                MineProduct entity = res.get(i);
                LatLonPoint endPoint = new LatLonPoint(Double.valueOf(entity.getLatitude()),Double.valueOf(entity.getLongitude()));
                double distance1 = showDistance(startPoint,endPoint)/1000;
                if(distance1 <= distance){
                    dei.add(k,distance1);
                    list.add(entity);
                    k++;
                }
            }
            Double del [] = new Double [dei.size()];
            for(int i=0;i<dei.size();i++){
                del[i]=dei.get(i);
            }
            //对数组升序
            Arrays.sort(del);

            //取出前10个
            List<Double> dei2 = new ArrayList<Double>();
            if(del.length<10){
                for(int n=0;n<del.length;n++){
                    dei2.add(n,del[n]);
                }
            }else{
                for(int n=0;n<10;n++){
                    dei2.add(n,del[n]);
                }
            }

            //取出对应的实体
            for(int i = 0;i<list.size();i++){
                MineProduct entity = list.get(i);
                LatLonPoint endPoint = new LatLonPoint(Double.valueOf(entity.getLatitude()),Double.valueOf(entity.getLongitude()));
                double distance1 = showDistance(startPoint,endPoint)/1000;
                for(int j = dei2.size()-1 ; j>= 0;j--){
                    if(distance1 == dei2.get(j)){
                        if(result.size() == 10){
                            return  result;
                        }
                        result.add(entity);
                    }
                }
            }
//			Log.i("从数据库中取出的数据", list.size()+"");
            return  result;
        } catch (Exception e) {
            Log.e("getNearestDistance()", e.toString());
        }
        return result;
    }

    /**
     * 计算距离
     * @param startPoint
     * @param endPoint
     * @return
     */
    private static  double showDistance(LatLonPoint startPoint, LatLonPoint endPoint){
        LatLng startLatlng = new LatLng(startPoint.getLatitude(),startPoint.getLongitude());
        LatLng  endLatlng = new LatLng(endPoint.getLatitude(), endPoint.getLongitude());
        // 计算量坐标点距离
        double distance = AMapUtils.calculateLineDistance(startLatlng, endLatlng);
        return distance;
    }

    public List<MineProduct> getMine(String type){
        final double leftLat = latitude + Double.valueOf(new DecimalFormat("#.0000000").format(distance/111));
        final double leftLon = longitude + Double.valueOf(new DecimalFormat("#.0000000").format(distance/111));
        final double rightLat = latitude - Double.valueOf(new DecimalFormat("#.0000000").format(distance/111));
        final double rightLon = longitude - Double.valueOf(new DecimalFormat("#.0000000").format(distance/111));

        String sqlType;
        if (type.equals("all")){
            sqlType = "";
        }else{
            sqlType = " and mine_mouth_type = 2 order by distance asc limit 10";
        }
        String sql = "select * ,"+ buildDistanceQuery(latitude,longitude)+" * 6380 as distance from mine_product_model"+
                " where mine_mouth_latitude < "+ leftLat +
                " and mine_mouth_latitude > "+ rightLat +
                " and mine_mouth_longitude > "+ rightLon +
                " and mine_mouth_longitude < "+ leftLon + sqlType;
        Cursor cursor = OrmLiteDBUtil.liteOrm.getReadableDatabase().rawQuery(sql, null);
        List<MineProduct> res = new ArrayList<MineProduct>();
        while (cursor.moveToNext()) {
            MineProduct entity = new MineProduct();
            entity.setMineMouthId(cursor.getString(cursor.getColumnIndex("mine_mouth_id")));
            entity.setLatitude(cursor.getString(cursor.getColumnIndex("mine_mouth_latitude")));
            entity.setLongitude(cursor.getString(cursor.getColumnIndex("mine_mouth_longitude")));
            entity.setName(cursor.getString(cursor.getColumnIndex("mine_mouth_name")));
            entity.setAdress(cursor.getString(cursor.getColumnIndex("mine_mouth_adress")));
            entity.setType(cursor.getString(cursor.getColumnIndex("mine_mouth_type")));
            res.add(entity);
        }
        return res;
    }


    public  List<MineProduct> getMineMouth(){
        final double leftLat = latitude + Double.valueOf(new DecimalFormat("#.0000000").format(distance/111));
        final double leftLon = longitude + Double.valueOf(new DecimalFormat("#.0000000").format(distance/111));
        final double rightLat = latitude - Double.valueOf(new DecimalFormat("#.0000000").format(distance/111));
        final double rightLon = longitude - Double.valueOf(new DecimalFormat("#.0000000").format(distance/111));
        List<MineProduct> liseers = OrmLiteDBUtil.liteOrm.<MineProduct>query(new QueryBuilder<MineProduct>(MineProduct.class)
                .where("mine_mouth_latitude" + "<",new String []{leftLat+""})
                .where("mine_mouth_latitude" + ">",new String []{rightLat+""})
                .where("mine_mouth_longitude" + "<",new String []{leftLon+""})
                .where("mine_mouth_longitude" + "<",new String []{rightLon+""})
                .orderBy(buildDistanceQuery(latitude,longitude) +" * 6380 ").limit(0, 10));
//		OrmLog.i("从数据库中取出的数据", liseers.size()+"");

        //		String mySql = " ACOS(SIN(("+lat+" * 3.1415) / 180 ) *SIN(("+latitude+" * 3.1415) / 180 ) " +
        //				"+COS(("+lat+" * 3.1415) / 180 ) * COS(("+latitude+" * 3.1415) / 180 ) *COS(("+lon+"* 3.1415) / 180 - ("+longitude+" * 3.1415) / 180 ) ) * 6380 " ;
        //
        return liseers;
    }

    /**
     * Build query based on distance using spherical law of cosinus
     *
     * d = acos(sin(lat1).sin(lat2)+cos(lat1).cos(lat2).cos(long2?long1)).R
     * where R=6371 and latitudes and longitudes expressed in radians
     *
     * In Sqlite we do not have access to acos() sin() and lat() functions.
     * Knowing that cos(A-B) = cos(A).cos(B) + sin(A).sin(B)
     * We can determine a distance stub as:
     * d = sin(lat1).sin(lat2)+cos(lat1).cos(lat2).(cos(long2).cos(long1)+sin(long2).sin(long1))
     *
     * First comparison point being fixed, sin(lat1) cos(lat1) sin(long1) and cos(long1)
     * can be replaced by constants.
     *
     * Location aware table must therefore have the following columns to build the equation:
     * sinlat => sin(radians(lat))
     * coslat => cos(radians(lat))
     * coslng => cos(radians(lng))
     * sinlng => sin(radians(lng))
     *
     * Function will return a real between -1 and 1 which can be used to order the query.
     * Distance in km is after expressed from R.acos(result)
     *
     * @param latitude, latitude of search
     * @param longitude, longitude of search
     * @return selection query to compute the distance
     */
    private  String buildDistanceQuery(double latitude, double longitude) {
        final double coslat = Math.cos(deg2rad(latitude));
        final double sinlat = Math.sin(deg2rad(latitude));
        final double coslng = Math.cos(deg2rad(longitude));
        final double sinlng = Math.sin(deg2rad(longitude));

        final double COSLAT = Math.cos(deg2rad(latitude));
        final double SINLAT = Math.sin(deg2rad(latitude));
        final double COSLNG = Math.cos(deg2rad(longitude));
        final double SINLNG = Math.sin(deg2rad(longitude));

        //@formatter:off
        return "(" + coslat + "*" + COSLAT
                + "*(" + COSLNG + "*" + coslng
                + "+" + SINLNG + "*" + sinlng
                + ")+" + sinlat + "*" + SINLAT
                + ")";
        //@formatter:on
    }



    private  double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /**
     * 需要使用下面的公式转换为公里的距离：
     * @param result
     * @return
     */
    private  double convertPartialDistanceToKm(double result) {
        double x = Math.toRadians(result);
        return Math.acos(x) * 6371;
    }

}
