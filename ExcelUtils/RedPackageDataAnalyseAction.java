package com.jd.market.interact.vender.action.redPackage;

import com.jd.common.web.result.Result;
import com.jd.market.interact.api.domain.redpackage.dataAnalyse.RedPackageData;
import com.jd.market.interact.vender.action.base.MenuBaseAction;
import com.jd.market.interact.vender.action.base.MenuIndexEnumType;
import com.jd.market.interact.vender.domain.redPackage.json.RedPackageDataPV;
import com.jd.market.interact.vender.domain.redPackage.param.DataAnalyseParam;
import com.jd.market.interact.vender.service.promotion.redpackage.impl.dataAnalyse.RedPackageDataService;
import com.jd.market.interact.vender.utils.WriteExcelUtil;
import com.jd.pop.vender.center.domain.auth.VenderPrivilege;
import com.opensymphony.xwork2.Preparable;
import org.apache.commons.lang.time.DateFormatUtils;


import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maoyi on 2016/6/28.
 */
public class RedPackageDataAnalyseAction extends MenuBaseAction implements Preparable {

    @Resource
    private RedPackageDataService redPackageDataService;

    private DataAnalyseParam dataAnalyseParam;

    private InputStream inputStream;

    private String fileName;
    String resultPage = "";
    RedPackageDataPV sysData;

    @VenderPrivilege(code= RED_PACKAGE_AUTH_CODE)
    public String view(){
//        return "view";
        return SUCCESS;
    }
    @VenderPrivilege(code= RED_PACKAGE_AUTH_CODE)
    public String getAnalyseData(){
        if(!checkParam()){
            return SUCCESS;
        }
        sysData = redPackageDataService.getRedPackageData(dataAnalyseParam);
        return SUCCESS;
    }

    private boolean checkParam(){
        return !(null == dataAnalyseParam.getDayGrep() && ( null == dataAnalyseParam.getStartDate() || null == dataAnalyseParam.getEndDate()));
    }
    @VenderPrivilege(code= RED_PACKAGE_AUTH_CODE)
    public String downReport(){
        List<RedPackageData> dataList = redPackageDataService.getRedPackageDataList(dataAnalyseParam);
        Map<String,List> map = new HashMap<>();
        map.put("数据分析",dataList);
        Map<String,Map<String,String>> title = new HashMap<>();
        Map<String,String> temp = new HashMap<>();
        temp.put("dt","日期");
        temp.put("activityId","活动编码");
        temp.put("activityName","活动名称");
        temp.put("msgCount","消息推送量");
        temp.put("msgClickPv","消息点击量");
        temp.put("directOrderCount","订单引入量");
        title.put("数据分析",temp);
        try {
            fileName = "红包数据-" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".xls";
            fileName = new String(fileName.getBytes("UTF-8"),"ISO-8859-1");
            inputStream = WriteExcelUtil.writeExcel(map,title);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "downReport";
    }

    public void prepare() throws Exception {
        Result result = new Result();
        result.addDefaultModel("leftMenuId", MenuIndexEnumType.RED_PACKAGE_REPORT.getLeftMenuId());
        result.addDefaultModel("tableIndex", MenuIndexEnumType.RED_PACKAGE_REPORT.getTableIndex());
        result.addDefaultModel("thirdMenuId", MenuIndexEnumType.RED_PACKAGE_REPORT.getThirdMenuId());
        toVmMenu(result);
    }

    public RedPackageDataPV getSysData() {
        return sysData;
    }

    public void setSysData(RedPackageDataPV sysData) {
        this.sysData = sysData;
    }

    public String getResultPage() {
        return resultPage;
    }

    public DataAnalyseParam getDataAnalyseParam() {
        return dataAnalyseParam;
    }

    public void setDataAnalyseParam(DataAnalyseParam dataAnalyseParam) {
        this.dataAnalyseParam = dataAnalyseParam;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


}
