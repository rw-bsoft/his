<?xml version="1.0" encoding="UTF-8"?>
<#assign group=headers[0]>
<#assign labs= ['20','21','22','23','24','25','26','27','28','29','30','31','32','33','34','35','36','37','38','39','40','41']> 
<chart caption="${title}" subcaption="${subTitle}" anchorRadius="1" use3DLighting="1" lineThickness="1"  anchorBorderThickness="0" numberScaleValue="10000,10000" numberScaleUnit="万,亿" labelDisplay="none" xAxisName='${group.alias}'  yAxisName='' showValues='0' numVDivLines='10' legendPosition ="RIGHT">
	<categories>
	    <#list labs as lbl>
	    	<category label='${lbl}'/>
	    </#list>
	</categories>
	<#list headers as h>
		<#if h.func??>
			<dataset seriesName='${h.alias}' color="#0000FF">
			 <#list labs as lbl>
			   <#assign flag=false>
	    	   <#list rs as r>
					<#if r_index = limit>
						<#break>
					</#if>
					<#if lbl = r[group.id]!"">
						<#assign flag=true>
					    <set value='${r[h.id]}'/>	
						<#break>
					</#if>
				</#list>
				<#if flag=false>
					<set value=''/>
				</#if>
	   		 </#list>
			
			<#list rs as r>
				<#if r_index = limit>
					<#break>
				</#if>
				<set value='${r[h.id]}'
				<#if diggers??>
					link="javascript:FC_Click('${chartId}',${r_index},
					<#if diggers[h.id]??>
						'${h.id}'
					<#else>
						'${group.id}'
					</#if>
					)"
				</#if>
				/>	
			</#list>
			</dataset>
			<dataset seriesName='10th'>
				<set value='15.3'/>
				<set value='17.6'/>
				<set value='18.7'/>
				<set value='19.0'/>
				<set value='22.0'/>
				<set value='21.0'/>
				<set value='22.3'/>
				<set value='21.4'/>
				<set value='22.4'/>
				<set value='24.0'/>
				<set value='24.8'/>
				<set value='26.3'/>
				<set value='25.3'/>
				<set value='26.0'/>
				<set value='27.8'/>
				<set value='29.0'/>
				<set value='29.8'/>
				<set value='29.8'/>
				<set value='30.0'/>
				<set value='29.5'/>
				<set value='30.0'/>
				<set value='31.8'/>
			</dataset>
			<dataset seriesName='50th'>
				<set value='18.3'/>
				<set value='20.8'/>
				<set value='21.8'/>
				<set value='22.0'/>
				<set value='23.6'/>
				<set value='23.5'/>
				<set value='24.0'/>
				<set value='25.0'/>
				<set value='26.1'/>
				<set value='27.3'/>
				<set value='27.5'/>
				<set value='28.0'/>
				<set value='29.3'/>
				<set value='29.8'/>
				<set value='31.0'/>
				<set value='31.0'/>
				<set value='31.5'/>
				<set value='32.0'/>
				<set value='32.5'/>
				<set value='32.8'/>
				<set value='33.3'/>
				<set value='34.0'/>
			</dataset>
			<dataset seriesName='90th'>
				<set value='21.4'/>
				<set value='23.2'/>
				<set value='24.2'/>
				<set value='24.5'/>
				<set value='25.1'/>
				<set value='25.9'/>
				<set value='27.3'/>
				<set value='28.0'/>
				<set value='29.0'/>
				<set value='30.0'/>
				<set value='31.0'/>
				<set value='30.0'/>
				<set value='32.0'/>
				<set value='32.3'/>
				<set value='33.8'/>
				<set value='33.3'/>
				<set value='34.5'/>
				<set value='35.0'/>
				<set value='35.7'/>
				<set value='35.8'/>
				<set value='35.3'/>
				<set value='37.3'/>
			</dataset>
		</#if>
	</#list>
    <trendlines>
      <line startValue='10000000000' color='ff0000' displayValue='' showOnTop='1'/>
    </trendlines>
	<styles>
		<definition>
			<style name='caption' type='font' size='12' color='666666'/>
			<style name='subcaption' type='font' size='11' color='666666' bold='0'/>
			<style name='yaxis' type='font' font='@宋体' size='10' bold='0' />
			<style name='xaxis' type='font' size='10' bold='0' />
			<style name='labels' type='font' size='12' color='#000000'/>
		</definition>
		<application>
				<apply toObject='caption' styles='caption' />
				<apply toObject='subcaption' styles='subcaption' />
				<apply toObject='XAXISNAME' styles='xaxis' />
				<apply toObject='YAXISNAME' styles='yaxis' />
				<apply toObject='DATALABELS' styles="labels"/>
				<apply toObject='Legend' styles="labels"/>
				<apply toObject='TRENDVALUES' styles="labels"/> 
		</application>
	</styles>
</chart>	