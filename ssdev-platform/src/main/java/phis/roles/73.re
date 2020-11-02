<?xml version="1.0" encoding="UTF-8"?>
<role id="phis.73" name="医技角色" type="post" parent="base">
	<accredit>
		<apps acType="whitelist">
			<app id="phis.application.menu.COMM">
				<catagory id="PUB">
					<others />
				</catagory>
			</app>
			<app id="phis.application.top.TOP">
				<catagory id="TOPFUNC">
					<module id="MedicalSwitch">
						<others />
					</module>
				</catagory>
			</app>
			<app id="phis.application.menu.QKZL" acType="whitelist">
				<catagory id="MED" acType="blacklist">
					<module id="MED04">
						<others />
					</module>
					<module id="MED03">
						<others />
					</module>
				</catagory>
			</app>
			<app id="phis.application.menu.TJFX">
				<catagory id="MED">
					<others />
				</catagory>
			</app>
			<app id="phis.application.sys.SYS" acType="whitelist">
				<catagory id="YJ_CFG">
					<others />
				</catagory>
			</app>
		</apps>
		<service acType="whitelist"></service>
		<storage acType="whitelist">

			<!-- 门诊模块 -->
			<store id="phis.application.ivc.schemas.MS_HZRB_JZRQ" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.CZGH'],["$",'%user.userId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.cic.schemas.MS_BRDA_CIC" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JDJG'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>

			<store id="phis.application.cic.schemas.GY_CYZD_CIC" acValue="1111">
				<conditions>
					<condition type="filter">
						['and',['eq',['$','a.JGID'],["$",'%user.manageUnit.id']],['eq',['$','a.YGDM'],["$",'%user.userId']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>

			<store id="phis.application.cic.schemas.GY_ZLFA01" acValue="1111">
				<conditions>
					<condition type="filter">
						['and',['eq',['$','a.JGID'],["$",'%user.manageUnit.id']],['eq',['$','a.YGDM'],["$",'%user.userId']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>

			<store id="phis.application.cic.schemas.GY_BLMB_Z" acValue="1111">
				<conditions>
					<condition type="filter">
						['and',['eq',['$','a.JGID'],["$",'%user.manageUnit.id']],['eq',['$','a.YGDM'],["$",'%user.userId']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>

			<!-- 挂号模块 -->
			<store id="phis.application.reg.schemas.MS_GHKS" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.reg.schemas.MS_YSPB" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.reg.schemas.GY_YGDM_REG" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.ORGANIZCODE'],["$",'%user.manageUnit.ref']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<!--药库模块 -->
			<store id="phis.application.sto.schemas.YK_YPXX" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.YKSB'],["$",'%user.properties.storehouseId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.sto.schemas.YK_CDXX_JGGL" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.sto.schemas.YK_JZJL" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.XTSB'],["$",'%user.properties.storehouseId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.cic.schemas.YS_MZ_ZT01_CF" acValue="1111">
				<conditions>
					<condition type="filter">
						['and',['eq',['$','a.JGID'],["$",'%user.manageUnit.id']],['eq',['$','a.YGDM'],["$",'%user.userId']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.cic.schemas.YS_MZ_ZT01_XM" acValue="1111">
				<conditions>
					<condition type="filter">
						['and',['eq',['$','a.JGID'],["$",'%user.manageUnit.id']],['eq',['$','a.YGDM'],["$",'%user.userId']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.sto.schemas.YK_YKLB" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.sto.schemas.YK_RKFS" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.XTSB'],["$",'%user.properties.storehouseId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.sto.schemas.YK_CKFS" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.XTSB'],["$",'%user.properties.storehouseId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.sto.schemas.YK_CDXX_CSZC" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','e.YKSB'],["$",'%user.properties.storehouseId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.sto.schemas.YK_RK01_WQR" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.XTSB'],["$",'%user.properties.storehouseId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.sto.schemas.YK_RK02" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.XTSB'],["$",'%user.properties.storehouseId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.sto.schemas.YK_RK01_QT_WQR"
				acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.XTSB'],["$",'%user.properties.storehouseId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.sto.schemas.YK_RK02_QT" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.XTSB'],["$",'%user.properties.storehouseId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.sto.schemas.YK_PD01" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.XTSB'],["$",'%user.properties.storehouseId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<!--药房模块 -->
			<store id="phis.application.pha.schemas.YF_YPXX" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.pha.schemas.YF_YFLB" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>

			<store id="phis.application.pha.schemas.YK_YPXX" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.sto.schemas.YK_YPXX_BZ" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.pha.schemas.YF_RKFS" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.pha.schemas.YF_CKFS" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.pha.schemas.YF_YPXX" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.pha.schemas.YF_RK01" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.pha.schemas.YF_RK01_QR" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.pha.schemas.YF_CK01" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.pha.schemas.YF_CK01_QR" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.pha.schemas.YF_KCMX_CSH" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.pha.schemas.YF_KCMX_JY" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.pha.schemas.MS_CF01_YFFY" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.sto.schemas.YK_TJ01" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.sto.schemas.YK_TJ01_ZX" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>

			<store id="phis.application.pha.schemas.YF_JZJL" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.pha.schemas.YF_YK01_RQ" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.YFSB'],["$",'%user.properties.pharmacyId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<!--住院药房模块 -->
			<store id="BQ_TJ01_TJ" acValue="1111">
				<conditions>
					<condition type="filter">
						['and',['eq',['$','a.TJYF'],["$",'%user.prop.pharmacyId','d']],['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="BQ_TJ02_FY" acValue="1111">
				<conditions>
					<condition type="filter">
						['and',['eq',['$','d.TJYF'],["$",'%user.prop.pharmacyId','d']],['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="ZY_BRRY_BQTY" acValue="1111">
				<conditions>
					<condition type="filter">
						['and',['eq',['$','a.YFSB'],["$",'%user.prop.pharmacyId','d']],['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="YF_FYJL_LSCX" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="YF_ZYFYMX_LSCX" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<!--住院药房模块结束 -->
			<!-- 住院模块 -->
			<store id="phis.application.hos.schemas.ZY_CWSZ" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.war.schemas.ZY_BRRY_BQ" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.war.schemas.ZY_BQYZ_MER" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.war.schemas.ZY_BQYZ_PER" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.war.schemas.BQ_TYMX_ER" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.hos.schemas.ZY_YGPJ_FP" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.hos.schemas.ZY_YGPJ_JK" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.hos.schemas.ZY_BRRY_BRGL" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.hos.schemas.ZY_TBKK_CX" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.cic.schemas.GY_YLSF_AL" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','b.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.war.schemas.YS_MZ_ZT02_ZY" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','c.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.cic.schemas.YS_MZ_ZT02_CI" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','c.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<!--住院模块结束 -->
			<!-- 收费模块 -->
			<store id="MS_YGPJ_JZ" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>

			<store id="MS_YGPJ_MZ" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>

			<store id="MS_YGPJ_FP" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>



			<!-- 维护模块 -->
			<store id="phis.application.pub.schemas.GY_QXKZ_CFG" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>

			<store id="phis.application.pub.schemas.SYS_Personnel_CFG"
				acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>


			<store id="SYS_Personnel_YH" acValue="1111">
				<conditions>
					<condition type="filter">
						['and',['like', ['$','a.JGID'],
						['concat',['$','%user.manageUnit.id'],['s','%']]
						],['eq',['$','a.ZFPB'],["s",'0']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>

			<store id="PUB_PublicInfo" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.publishUnit'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>

			<store id="phis.application.cfg.schemas.GY_XTCS" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<!--机构收费项目维护 -->
			<store id="phis.application.cfg.schemas.GY_YLMX_DR" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<!--物资管理一级库房 -->
			<store id="phis.application.cfg.schemas.WL_ZBLB" acValue="1111">
				<conditions>
					<condition type="filter">
						['or',['and',['and',['eq',['$','a.JGID'],["$",'%user.properties.topUnitId']],['ne',['$','a.JGID'],["$",'%user.manageUnit.id']]],['eq',['$','ZBZT'],["i",1]]],['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>

			<store id="phis.application.cfg.schemas.WL_GHDW" acValue="1111">
				<conditions>
					<condition type="filter">
						['or',['eq',['$','a.JGID'],["$",'%user.manageUnit.id']],['eq',['$','a.JGID'],["$",'%user.properties.topUnitId']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>

			<store id="phis.application.cfg.schemas.WL_SCCJ" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>

			<store id="phis.application.cfg.schemas.WL_KFXX" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>

			<store id="phis.application.cfg.schemas.WL_FLLB" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>

			<store id="phis.application.cfg.schemas.WL_HSQX_YG" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.cfg.schemas.WL_HSQX_KS" acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="phis.application.cfg.schemas.SYS_Office_SELECT"
				acValue="1111">
				<conditions>
					<condition type="filter">
						['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<others acValue="1111" />
		</storage>
	</accredit>
</role>
