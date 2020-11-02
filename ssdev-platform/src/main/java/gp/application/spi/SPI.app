<?xml version="1.0" encoding="UTF-8"?>
<application id="gp.application.spi.SPI" name="特殊人群"  type="1">
	<catagory id="SPI" name="特殊人群">
		<module id="SPI01" name="残疾人随访人数" script="gp.application.spi.script.SpecialPeopleCJRModule"> 
			<properties>  
				<p name="notVisitRecord">gp.application.spi.SPI/SPI/SPI01_01</p>
			</properties>  
			<action id="query" name="查询" iconCls="query"/>
			<action id="notVisitRecord" name="未随访人数" iconCls="query"/>  
			<action id="cancel" name="取消" iconCls="common_cancel"/>  
		</module>
		<module id="SPI01_01" name="残疾人未随访人数" script="gp.application.spi.script.SpecialPeopleCJRNotVisitList"> 
			<properties>
				<p name="entryName">gp.application.spi.schemas.SPI_SpecialPeopleCJRRecord</p>  
				<p name="listServiceId">gp.specialPeopleInfoService</p> 
				<p name="serviceAction">queryCJRNotVisitRecord</p> 
			</properties>  
			<action id="query" name="查询" iconCls="query"/>  
			<action id="visit" name="随访" iconCls="hypertension_visit"/>
			<action id="cancel" name="取消" iconCls="common_cancel"/>  
		</module>
		<module id="SPI01_02" name="残疾人实际签约人数" script="gp.application.spi.script.SpecialPeopleCJRSignList"> 
			<properties>
				<p name="entryName">gp.application.spi.schemas.SPI_SpecialPeopleCJRSignRecord</p>  
				<p name="listServiceId">gp.specialPeopleInfoService</p> 
				<p name="serviceAction">queryCJRSignRecord</p> 
			</properties>  
			<action id="query" name="查询" iconCls="query"/> 
			<action id="cancel" name="取消" iconCls="common_cancel"/>   
			<action id="queryRecord" name="档案查询" iconCls="query"/> 
			<action id="visit" name="随访信息" iconCls="hypertension_visit"/>
			
		</module>
		<module id="SPI02" name="离休干部随访人数" script="gp.application.spi.script.SpecialPeopleLXGBModule"> 
			<properties>  
				<p name="notVisitRecord">gp.application.spi.SPI/SPI/SPI02_01</p>
			</properties>  
			<action id="query" name="查询" iconCls="query"/>
			<action id="notVisitRecord" name="未随访人数" iconCls="query"/>  
			<action id="cancel" name="取消" iconCls="common_cancel"/>  
		</module>
		<module id="SPI02_01" name="离休干部未随访人数" script="gp.application.spi.script.SpecialPeopleLXGBNotVisitList"> 
			<properties>
				<p name="entryName">gp.application.spi.schemas.SPI_SpecialPeopleLXGBRecord</p>  
				<p name="listServiceId">gp.specialPeopleInfoService</p> 
				<p name="serviceAction">queryLXGBNotVisitRecord</p> 
			</properties>  
			<action id="query" name="查询" iconCls="query"/>  
			<action id="visit" name="随访" iconCls="hypertension_visit"/>
			<action id="cancel" name="取消" iconCls="common_cancel"/>  
		</module>
		<module id="SPI02_02" name="离休干部实际签约人数" script="gp.application.spi.script.SpecialPeopleLXGBSignList"> 
			<properties>
				<p name="entryName">gp.application.spi.schemas.SPI_SpecialPeopleLXGBSignRecord</p>  
				<p name="listServiceId">gp.specialPeopleInfoService</p> 
				<p name="serviceAction">queryLXGBSignRecord</p> 
			</properties>  
			<action id="query" name="查询" iconCls="query"/> 
			<action id="cancel" name="取消" iconCls="common_cancel"/>   
			<action id="queryRecord" name="档案查询" iconCls="query"/> 
			<action id="visit" name="随访信息" iconCls="hypertension_visit"/>
		</module>
		<module id="SPI03" name="老年人随访人数" script="gp.application.spi.script.SpecialPeopleLNRModule"> 
			<properties>  
				<p name="notVisitRecord">gp.application.spi.SPI/SPI/SPI03_01</p>
			</properties>  
			<action id="query" name="查询" iconCls="query"/>  
			<action id="notVisitRecord" name="未随访人数" iconCls="query"/>
			<action id="cancel" name="取消" iconCls="common_cancel"/>  
		</module>
		<module id="SPI03_01" name="老年人未随访人数" script="gp.application.spi.script.SpecialPeopleLNRNotVisitList"> 
			<properties>
				<p name="entryName">gp.application.spi.schemas.SPI_SpecialPeopleLNRRecord</p>  
				<p name="listServiceId">gp.specialPeopleInfoService</p> 
				<p name="serviceAction">queryLNRNotVisitRecord</p> 
			</properties>  
			<action id="query" name="查询" iconCls="query"/>  
			<action id="visit" name="随访" iconCls="hypertension_visit"/>
			<action id="cancel" name="取消" iconCls="common_cancel"/>  
		</module>
		<module id="SPI03_02" name="老年人实际签约人数" script="gp.application.spi.script.SpecialPeopleLNRSignList"> 
			<properties>
				<p name="entryName">gp.application.spi.schemas.SPI_SpecialPeopleLNRSignRecord</p>  
				<p name="listServiceId">gp.specialPeopleInfoService</p> 
				<p name="serviceAction">queryLNRSignRecord</p> 
			</properties>  
			<action id="query" name="查询" iconCls="query"/> 
			<action id="cancel" name="取消" iconCls="common_cancel"/>   
			<action id="queryRecord" name="档案查询" iconCls="query"/> 
			<action id="visit" name="随访信息" iconCls="hypertension_visit"/>
			
		</module>
		<module id="SPI04" name="精神病随访人数" script="gp.application.spi.script.SpecialPeopleJSBModule"> 
			<properties>  
				<p name="notVisitRecord">gp.application.spi.SPI/SPI/SPI04_01</p>
			</properties>  
			<action id="query" name="查询" iconCls="query"/>  
			<action id="notVisitRecord" name="未随访人数" iconCls="query"/>
			<action id="cancel" name="取消" iconCls="common_cancel"/>  
		</module>
		<module id="SPI04_01" name="精神病未随访人数" script="gp.application.spi.script.SpecialPeopleJSBNotVisitList"> 
			<properties>
				<p name="entryName">gp.application.spi.schemas.SPI_SpecialPeopleJSBRecord</p>  
				<p name="listServiceId">gp.specialPeopleInfoService</p> 
				<p name="serviceAction">queryJSBNotVisitRecord</p> 
			</properties>  
			<action id="query" name="查询" iconCls="query"/>  
			<action id="visit" name="随访" iconCls="hypertension_visit"/>
			<action id="cancel" name="取消" iconCls="common_cancel"/>  
		</module>
		<module id="SPI04_02" name="精神病实际签约人数" script="gp.application.spi.script.SpecialPeopleJSBSignList"> 
			<properties>
				<p name="entryName">gp.application.spi.schemas.SPI_SpecialPeopleJSBSignRecord</p>  
				<p name="listServiceId">gp.specialPeopleInfoService</p> 
				<p name="serviceAction">queryJSBSignRecord</p> 
			</properties>  
			<action id="query" name="查询" iconCls="query"/> 
			<action id="cancel" name="取消" iconCls="common_cancel"/>   
			<action id="queryRecord" name="档案查询" iconCls="query"/> 
			<action id="visit" name="随访信息" iconCls="hypertension_visit"/>
			
		</module>
	</catagory>
</application>