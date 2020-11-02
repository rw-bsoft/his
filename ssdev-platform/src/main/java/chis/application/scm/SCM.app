<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.scm.SCM" name="签约管理" type="1">
    <catagory id="SCC" name="签约配置">
        <module id="SCC01" name="签约项目维护" script="chis.application.scm.item.script.FamilyContractServiceList">
            <properties>
                <p name="entryName">chis.application.scm.schemas.SCM_ServiceItems</p>
                <p name="navDic">chis.dictionary.serviceItems</p>
                <p name="navParentKey">0</p>
                <p name="navField">parentCode</p>
            </properties>
        </module>
        <module id="SCC02" name="签约包维护" script="chis.application.scm.bag.script.ServicePackageListView">
            <properties>
                <p name="entryName">chis.application.scm.schemas.SCM_ServicePackage</p>
                <p name="refModule">chis.application.scm.SCM/SCC/SCC02_01</p>
            </properties>
            <action id="createSP" name="新建" iconCls="create"/>
            <action id="modify" name="查看" iconCls="update"/>
            <action id="logOff" name="注销/启用" iconCls="remove"/>
        </module>
        <module id="SCC02_01" name="签约包维护" type="1" script="chis.application.scm.bag.script.ServicePackageModule">
            <action id="SPForm" name="服务包" ref="chis.application.scm.SCM/SCC/SCC02_0101" type="tab"/>
            <action id="SPIModule" name="所属项目" ref="chis.application.scm.SCM/SCC/SCC02_0102" type="tab"/>
        </module>
        <module id="SCC02_0101" name="包信息" type="1" script="chis.application.scm.bag.script.ServicePackageForm">
            <properties>
                <p name="entryName">chis.application.scm.schemas.SCM_ServicePackage</p>
            </properties>
            <action id="save" name="保存"/>
        </module>
        <module id="SCC02_0102" name="包所属项目" type="1" script="chis.application.scm.bag.script.ServiceItemsModule">
            <action id="SPLeft" name="未选中" ref="chis.application.scm.SCM/SCC/SCC02_0103" type="tab"/>
            <action id="SPRight" name="已选中" ref="chis.application.scm.SCM/SCC/SCC02_0104" type="tab"/>
            <!--<action id="createItem" name="新建" iconCls="create"/>
            <action id="remove" name="删除"/>
            <action id="saveModifyRecords" name="保存修改" iconCls="save"/>-->
        </module>
        <module id="SCC02_0103" name="包所属项目" type="1"
                script="chis.application.scm.bag.script.ServicePackageItemsListLeft">
            <properties>
                <p name="entryName">chis.application.scm.schemas.SCM_ServiceItems_package</p>
            </properties>
        </module>
        <module id="SCC02_0104" name="包所属项目" type="1"
                script="chis.application.scm.bag.script.ServicePackageItemsListRight">
            <properties>
                <p name="entryName">chis.application.scm.schemas.SCM_ServicePackageItems</p>
            </properties>
            <action id="save" name="保存" iconCls="save"/>
        </module>


        <module id="SCC03" name="医疗服务项维护" script="chis.application.scm.item.script.HisFamilyContractServiceList" type="1">
            <properties>
                <p name="entryName">chis.application.scm.schemas.SCM_ServiceItems</p>
                <p name="navDic">chis.dictionary.serviceItems</p>
                <p name="navParentKey">2</p>
                <p name="navField">parentCode</p>
            </properties>
            <action id="createSI" name="新建" iconCls="create"/>
            <action id="update" name="查看"/>
            <!--<action id="removeSI" name="删除" iconCls="remove"/>-->
        </module>

        <module id="SCC0304" name="医疗服务项维护1" script="chis.application.scm.item.script.HisServicePackageListView" type="1">
            <properties>
                <p name="entryName">chis.application.scm.schemas.SCM_ServiceItems_his</p>
                <p name="refModule">chis.application.scm.SCM/SCC/SCC03_01</p>
                <p name="initCnd">['and',['eq',['$','a.itemNature'],["$",'2']],['ne',['$','a.isBottom'],['s','y']]]</p>
            </properties>
            <action id="createSP" name="新建" iconCls="create"/>
            <action id="modify" name="查看" iconCls="update"/>
            <action id="logOff" name="注销/启用" iconCls="remove"/>
        </module>


        <module id="SCC03_01" name="服务项维护" type="1" script="chis.application.scm.item.script.HisServicePackageModule">
            <action id="SPForm" name="服务项" ref="chis.application.scm.SCM/SCC/SCC03_0101" type="tab"/>
            <action id="SPIModule" name="所属项目" ref="chis.application.scm.SCM/SCC/SCC03_0102" type="tab"/>
        </module>
        <module id="SCC03_0101" name="包信息" type="1" script="chis.application.scm.item.script.HisServicePackageForm">
            <properties>
                <p name="entryName">chis.application.scm.schemas.SCM_ServiceItems_his</p>
            </properties>
            <action id="save" name="保存"/>
        </module>
        <module id="SCC03_0102" name="收费项目" type="1" script="chis.application.scm.item.script.HisServiceItemsModule">
            <action id="SPLeft" name="未选中" ref="chis.application.scm.SCM/SCC/SCC03_0103" type="tab"/>
            <action id="SPRight" name="已选中" ref="chis.application.scm.SCM/SCC/SCC03_0104" type="tab"/>
            <!--<action id="createItem" name="新建" iconCls="create"/>
            <action id="remove" name="删除"/>
            <action id="saveModifyRecords" name="保存修改" iconCls="save"/>-->
        </module>
        <module id="SCC03_0103" name="包所属项目" type="1" script="chis.application.scm.item.script.HisServicePackageItemsListLeft">
            <properties>
                <p name="entryName">chis.application.scm.schemas.SCM_GY_YLMX</p>
                <p name="serviceId">chis.signContractRecordService</p>
            </properties>
        </module>
        <module id="SCC03_0104" name="包所属项目" type="1" script="chis.application.scm.item.script.HisServicePackageItemsListRight">
            <properties>
                <p name="entryName">chis.application.scm.schemas.SCM_ServiceItems_his</p>
            </properties>
            <action id="save" name="保存" iconCls="save"/>
        </module>
        <module id="SCC03_02" name="新增服务项目" type="1" script="chis.application.scm.item.script.UpdateContractServiceForm">
            <properties>
                <p name="entryName">chis.application.scm.schemas.SCM_ServiceItems</p>
            </properties>
            <!--<action id="save" name="保存"/>-->
            <!--<action id="cancel" name="取消"/>-->
        </module>

    </catagory>
    <catagory id="SCM" name="签约管理">
        <module id="SCM01" name="签约服务记录" script="chis.application.scm.sign.script.SignContractRecordListView">
            <properties>
                <p name="entryName">chis.application.scm.schemas.SCM_SignContractRecord_List</p>
                <p name="serviceId">chis.signContractRecordService</p>         
                <p name="refModule">chis.application.scm.SCM/SCM/SCM01_03</p>
            </properties>
            <action id="createSC" name="签约" iconCls="create"/>
            <action id="stopSC" name="解约" iconCls="remove"/>
            <action id="modify" name="查看" iconCls="update"/>
            <action id="print"  name="导出" />
        </module>
        <module id="SCM01_01" name="居民签约" type="1" script="chis.application.scm.sign.script.PersonalSignContractModule">
            <action id="pscList" name="个人签约列表" ref="chis.application.scm.SCM/SCM/SCM01_0101"/>
            <action id="SCModule" name="签约" ref="chis.application.scm.SCM/SCM/SCM01_0102"/>
        </module>
        <module id="SCM01_0101" name="个人签约列表" type="1"
                script="chis.application.scm.sign.script.PersonalSignContractList">
            <properties>
                <p name="entryName">chis.application.scm.schemas.PersonalSignContractList</p>
            </properties>
            <action id="new" name="签约"/>
            <action id="supplementary" name="补签"/>
            <!--<action id="renewal" name="续约" iconCls="update"/>-->
            <!--<action id="remove" name="删除签约" />-->
        </module>
        <module id="SCM01_0102" name="签约" type="1" script="chis.application.scm.sign.script.SignContractRecordModule">
            <properties>
                <p name="saveServiceId">chis.signContractRecordService</p>
                <p name="saveAction">saveSignContract</p>
            </properties>
            <action id="SCBag" name="签约包列表" ref="chis.application.scm.SCM/SCM/SCM01_0102_01"/>
            <action id="SCInfo" name="签约信息" ref="chis.application.scm.SCM/SCM/SCM01_0102_02"/>
          
        </module>
        <module id="SCM01_0102_01" name="签约信息" type="1"
                script="chis.application.scm.sign.script.SignContractRecordForm">
            <properties>
                <p name="entryName">chis.application.scm.schemas.SCM_SignContractForm</p>
            </properties>
        </module>
        <module id="SCM01_0102_02" name="签约服务包" type="1"
                script="chis.application.scm.sign.script.SignContractPackageList">
            <properties>
                <p name="entryName">chis.application.scm.schemas.SCM_SignContractCheckPackage</p>
            </properties>
            <action id="save" name="保存" iconCls="save"/>
            <action id="stop" name="解约" iconCls="remove"/>
            <action id="printProcotol" name="协议书" iconCls="print"/>
        </module>
        <module id="SCM01_03" name="居民签约" type="1"
                script="chis.application.scm.sign.script.PersonalSignContractModule2">
            <action id="psctForm" name="个人基本信息" ref="chis.application.scm.SCM/SCM/SCM01_03_01"/>
            <action id="SCModule" name="签约" ref="chis.application.scm.SCM/SCM/SCM01_01"/>
        </module>
        <module id="SCM01_03_01" name="个人信息" type="1"
                script="chis.application.scm.sign.script.PersonalContractInfoForm">
            <properties>
                <p name="entryName">chis.application.scm.schemas.SCM_PersonalContractInfoForm</p>
            </properties>
        </module>
        <module id="SCM01_0102_01" name="签约信息" type="1"
                script="chis.application.scm.sign.script.SignContractRecordForm">
            <properties>
                <p name="entryName">chis.application.scm.schemas.SCM_SignContractForm</p>
            </properties>
        </module>
        <module id="SCM02" name="履约服务记录" script="chis.application.scm.sr.script.SignContractServiceModule">
            <action id="SCBag" name="人员签约列表" ref="chis.application.scm.SCM/SCM/SCM02_01"/>
            <action id="SCInfo" name="履约服务记录" ref="chis.application.scm.SCM/SCM/SCM02_02"/>
        </module>
        <module id="SCM02_01" type="1" name="签约人员列表" script="chis.application.scm.sr.script.SrContractPersonsListView">
            <properties>
                <p name="entryName">chis.application.scm.schemas.SCM_SignContractRecord</p>
            </properties>
        </module>
        <module id="SCM02_02" type="1" name="项目服务记录列表" script="chis.application.scm.sr.script.SrContractRecordListView">
            <properties>
                <p name="entryName">chis.application.scm.schemas.SCM_ServiceContractPlanTask</p>
                <p name="refModule">chis.application.scm.SCM/SCM/SCM02_02_01</p>
                <p name="refServiceRecord">chis.application.scm.SCM/SCM/SCM02_02_02</p>
            </properties>
            <action id="editor" name="手动履约" iconCls="create"/>
            <action id="query" name="查看履约记录" iconCls="query"/>
            <action id="tnbscbg" name="糖尿病并发症筛查报告" iconCls="query"/>
        </module>
        <module id="SCM02_02_01" type="1" name="新增服务记录" script="chis.application.scm.sr.script.NewServiceForm">
            <properties>
                <p name="entryName">chis.application.scm.schemas.SCM_NewService</p>
            </properties>
            <action id="save" name="保存"/>
        </module>
        <module id="SCM02_02_02" type="1" name="履约记录" script="chis.application.scm.sr.script.SrContractServiceRecordListView">
            <properties>
                <p name="entryName">chis.application.scm.schemas.SCM_NewServiceQuery</p>
                <p name="refModule">chis.application.scm.SCM/SCM/SCM02_02_01</p>
            </properties>
            <action id="editor" name="修改" iconCls="update"/>
        </module>
        <module id="SCM01_03_01_01" type="1" name="家庭成员列表"
                script="chis.application.scm.sign.script.FamilyMemberList">
            <properties>
                <p name="entryName">chis.application.scm.schemas.MPI_DemographicInfoFamily</p>
            </properties>
        </module>
		<module id="SCM03" name="签约服务统计(按人群分类)" 
			script="chis.application.scm.sr.script.QueryQyfw_RQFL">
		</module>
				
		<!--2020-09-10 增加家庭医生工作室服务记录表 -->
		<module id="HQ05" name="工作室服务记录" script="chis.application.scm.script.JYFWList">
			<properties>
				<p name="entryName">chis.application.scm.schemas.JYFWJL</p>
				<p name="removeServiceId">chis.simpleRemove</p>
			</properties>
            <action id="createRecord" name="新建" iconCls="create"/>
            <action id="remove" name="删除" iconCls="remove"/>
            <action id="modify" name="查看" iconCls="update"/>
            <action id="print"  name="导出" />
           <!-- <action id="check" name="审核" iconCls="update"/> -->
		</module>
		<module id="HQ06" name="家医服务记录整体" script="chis.application.scm.script.JYFWModule" type="1">
			<properties>
				<p name="autoLoadData">false</p>
			</properties>
			<action id="JYFWList" name="家医服务记录列表" ref="chis.application.scm.SCM/SCM/HQ0601" />
			<action id="JYFWForm" name="家医服务记录记录" ref="chis.application.scm.SCM/SCM/HQ0602" />
		</module>
		<module id="HQ0601" name="家医服务记录列表" script="chis.application.scm.script.JYFWPersonalList" type="1">
			<properties>
				<p name="entryName">chis.application.scm.schemas.JYFWJL</p>
				<p name="selectFirst">true</p>
				<p name="autoLoadData">false</p>
				<p name="disablePagingTbr">true</p>
			</properties>
		</module>
		<module id="HQ0602" name="家医服务记录表单" script="chis.application.scm.script.JYFWForm" icon="default" type="1">
			<properties>
				<p name="entryName">chis.application.scm.schemas.JYFWJL</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
			<action id="add" name="新增" group="create" />
		</module>
		<module id="TJ00" name="履约统计记录" script="chis.application.scm.tj.script.LYTJModule">
        </module>
        <module id="TJ10" name="履约未建档统计" script="chis.application.scm.tj.script.JMFYModule">
        </module>
        <module id="TJ01" name="核心指标汇总" script="chis.application.scm.tj.script.HXZBHZModule">
        </module>
        <module id="TJ02" name="核心指标明细" script="chis.application.scm.tj.script.HXZBMXModule">
        </module>
        <module id="TJ03" name="签约覆盖率" script="chis.application.scm.tj.script.QYFGLModule">
        </module>
        <module id="TJ04" name="服务包分类统计" script="chis.application.scm.tj.script.FWBFLModule">
        </module>
        <module id="TJ05" name="签约情况统计(月)" script="chis.application.scm.tj.script.QYQKYModule">
        </module>
        <module id="TJ06" name="签约服务报表" script="chis.application.scm.tj.script.QYFWBBModule">
        </module>
        <module id="TJ07" name="转诊统计" script="chis.application.scm.tj.script.JYZZModule">
        </module>
		<!--2020-09-23 运营-短信发送统计 -->
		<module id="TJ08" name="运营-短信发送统计" script="chis.application.scm.tj.script.DXFSModule">
		</module>
		<!-- 运营-减免费用统计统计 -->
        <module id="TJ09" name="运营-减免费用统计" script="chis.application.scm.tj.script.JMFYModule">
        </module>
    </catagory>
</application>