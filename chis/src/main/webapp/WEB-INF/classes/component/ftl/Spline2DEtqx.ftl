<?xml version="1.0" encoding="UTF-8"?>
<#assign group=headers[0]>
<chart animation='0' caption="${title}"  use3DLighting="1" lineThickness="0.7" showShadow ="0" anchorRadius="1" anchorBorderThickness="0.1" numberScaleValue="10000,10000" numberScaleUnit="aa,bb" labelDisplay="none" xAxisName='${group.alias}'  yAxisName='' showValues='0' yAxisMinValue='40' legendPosition ="BOTTOM">
	<categories>
	<#list rs as r>
		<#if r_index = limit>
			<#break>
		</#if>
		<#if group.dic??>
			<category  label='${r[group.id + "_text"]!""}'/>
		<#else>
		<#assign aaab=r[group.id]+"a">
			<#if aaab="1"+"a">
				<category  label='ab'/>
			<#else>
				<category  label='${r[group.id]!""}'/>
			</#if>
		</#if>
	</#list>
	</categories>
	<#list headers as h>
		<#if h.func??>
			<dataset seriesName='${h.alias}' color='${h.color!""}'>
			<#list rs as r>
				<#if r_index = limit>
					<#break>
				</#if>
				<set value='${r[h.id]}' color='${h.color!""}'
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
		</#if>
	</#list>
    <trendlines>
      <line startValue='860' color='ff0000' displayValue='' showOnTop='1'/>
    </trendlines>
	<styles>
		<definition>
			<style name='caption' type='font' size='12' color='666666'/>
			<style name='subcaption' type='font' size='11' color='666666' bold='0'/>
			<style name='yaxis' type='font' font='@宋体' size='8' bold='0' />
			<style name='xaxis' type='font' size='8' bold='0' />
			<style name='labels' type='font' size='8' color='#000000'/>
			<style name='datavalue' type='font' size='10' color='#3F8020'/>
		</definition>
		<application>
				<apply toObject='caption' styles='caption' />
				<apply toObject='subcaption' styles='subcaption' />
				<apply toObject='XAXISNAME' styles='xaxis' />
				<apply toObject='YAXISNAME' styles='yaxis' />
				<apply toObject='DATALABELS' styles="labels"/>
				<apply toObject='TRENDVALUES' styles="labels"/> 
				<apply toObject='DATAVALUES' styles="datavalue"/> 
				<apply toObject='YAXISVALUES' styles="labels"/> 
		</application>
	</styles>
</chart>	