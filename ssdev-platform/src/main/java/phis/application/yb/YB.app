<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.yb.YB" name="医保管理">
	<catagory id="YB" name="医保管理">
		<!--读卡界面.由于框架问题,可能每个模块的读卡界面都要单独写一边,可以直接继承该js-->
		<module id="YB01" name="读卡" script="phis.application.yb.sgyb.script.ReadeCardform">
			<properties>
				<p name="entryName">phis.application.yb.schemas.YB_YBKXX_SGYB</p>
			</properties>
			<action id="qd" name="确定" />
			<action id="dk" name="读卡" />
			<action id="test" name="测试" />
			<action id="qx" name="取消" />
		</module>
		<module id="YB02" name="医保对照" 
			script="phis.script.TabModule">
			<properties>
			</properties>
			<action id="ypdz"  name="药品对照" ref="phis.application.yb.YB/YB/YB0201" />
			<action id="fydz"  name="费用对照" ref="phis.application.yb.YB/YB/YB0202" />
			<action id="rydz"  name="人员对照" ref="phis.application.yb.YB/YB/YB0203" />
			<action id="rsss"  name="处方药品对照" ref="phis.application.yb.YB/YB/YB0204" />
            <action id="jcbw"  name="检查部位对照" ref="phis.application.yb.YB/YB/YB0205" />		
		</module>
		<module id="YB0201" name="药品对照" type="1"
			script="phis.application.yb.script.YpdzModule">
			<properties>
				<p name="refLeftList">phis.application.yb.YB/YB/YB020101</p>
				<p name="serviceId">yBService</p>
				<p name="saveActionId">saveYbypdz</p>
			</properties>
			<action id="sx" name="刷新"  iconCls="refresh"/>
			<action id="save" name="保存" />
			<action id="print" name="导出" iconCls="print"/>
		</module>
		<module id="YB020101" name="药品对照List" type="1"
			script="phis.application.yb.script.YpdzLeftList">
			<properties>
				<p name="entryName">phis.application.yb.schemas.YK_CDXX_YB</p>
				<p name="initCnd">['eq',['$','a.JGID'],['$','%user.manageUnit.id']]</p>
				<p name="fullServiceId">phis.yBService</p>
				<p name="serviceAction">queryYbypdz</p>
			</properties>
		</module>
		<module id="YB0202" name="项目对照" type="1"
			script="phis.application.yb.script.xmdzModule">
			<properties>
				<p name="refLeftList">phis.application.yb.YB/YB/YB020201</p>
				<p name="serviceId">yBService</p>
				<p name="saveActionId">saveYbxmdz</p>
			</properties>
			<action id="sx" name="刷新"  iconCls="refresh"/>
			<action id="save" name="保存" />
			<action id="print" name="导出" iconCls="print"/>
		</module>
		<module id="YB020201" name="项目对照List" type="1"
			script="phis.application.yb.script.xmdzLeftList">
			<properties>
				<p name="entryName">phis.application.yb.schemas.GY_YLMX_YB</p>
				<p name="initCnd">['eq',['$','a.JGID'],['$','%user.manageUnit.id']]</p>
				<p name="fullServiceId">phis.yBService</p>
				<p name="serviceAction">queryYbxmdz</p>
			</properties>
		</module>
		<module id="YB0203" name="人员对照" type="1"
			script="phis.application.yb.script.rydzModule">
			<properties>
				<p name="refLeftList">phis.application.yb.YB/YB/YB020301</p>
				<p name="serviceId">yBService</p>
				<p name="saveActionId">saveYbrydz</p>
			</properties>
			<action id="sx" name="刷新"  iconCls="refresh"/>
			<action id="save" name="保存" />
		</module>
		<module id="YB020301" name="人员对照List" type="1"
			script="phis.application.yb.script.rydzLeftList">
			<properties>
				<p name="entryName">phis.application.yb.schemas.SYS_PERSONNEL_YB</p>
				<p name="initCnd">['eq',['$','a.ORGANIZCODE'],['$','%user.manageUnit.Ref']]</p>
				<p name="fullServiceId">phis.yBService</p>
				<p name="serviceAction">queryYbrydz</p>
			</properties>
		</module>
		<module id="YB0204" name="处方药品对照" type="1"
            script="phis.application.yb.script.typkModule">
            <properties>
                <p name="refLeftList">phis.application.yb.YB/YB/YB020401</p>
                <p name="serviceId">yBService</p>
                <p name="saveActionId">savetypk</p>
            </properties>
            <action id="sx" name="刷新"  iconCls="refresh"/>
            <action id="save" name="保存" />
        </module>
        <module id="YB020401" name="人员对照List" type="1"
            script="phis.application.yb.script.typkLeftList">
            <properties>
                <p name="entryName">phis.application.yb.schemas.YB_TYPK</p>

            </properties>
        </module>

        <module id="YB0205" name="检查部位对照" type="1"
            script="phis.application.yb.script.jcbwModule">
            <properties>
                <p name="refLeftList">phis.application.yb.YB/YB/YB020501</p>
                <p name="serviceId">yBService</p>
                <p name="saveActionId">saveJCBW</p>
            </properties>
            <action id="sx" name="刷新"  iconCls="refresh"/>
            <action id="save" name="保存" />
        </module>
        <module id="YB020501" name="人员对照List" type="1"
            script="phis.application.yb.script.jcbwLeftList">
            <properties>
                <p name="entryName">phis.application.yb.schemas.YB_JCBW</p>
            </properties>
        </module>
	</catagory>
</application>