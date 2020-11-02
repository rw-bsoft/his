<?xml version="1.0" encoding="UTF-8"?>
<#assign group=headers[0]>
<chart caption="${title}" subcaption="${subTitle}" canvasBorderThickness="0.1" canvasBorderAlpha="50" use3DLighting="1" lineThickness="1" anchorBorderThickness="1" BorderThickness="1" borderColor="#666666" xAxisName='${group.alias}'  yAxisName='' showValues='0' yAxisMinValue='40' showLegend="1" legendNumColumns="1" legendPosition="RIGHT" labelDisplay='Rotate' slantLabels='1' >
	<categories>
	<#list rs as r>
		<#if r_index = limit>
			<#break>
		</#if>
		<#if group.dic??>
		<category label='${r[group.id + "_text"]!""}'/>
		<#else>
		<category label='${r[group.id]!""}'/>
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
		<#if r[h.id]??>
		<set value='${r[h.id]}' color='${h.color!""}' link="javascript:FC_Click('${chartId}',${r_index},'${group.id}','${r[group.id]}')"/>	
		<#else>
		<set/>
        </#if>
					</#list>
	</dataset>
		</#if>
	</#list>
	<trendlines>
		<line startValue='10000000000' color='ff0000' displayValue='' showOnTop='1' size="1"/>
	</trendlines>
	<styles>
		<definition>
			<style name='caption' type='font' size='12' color='666666'/>
			<style name='subcaption' type='font' size='11' color='666666' bold='0'/>
			<style name='yaxis' type='font' font='@宋体' size='8' bold='0' />
			<style name='xaxis' type='font' size='10' bold='0' />
			<style name='labels' type='font' size='5' color='#000000'/>
			<style name='LegendFont' type='font' size='10' color='#000000'/>
			<style name='labels2' type='font' size='10' color='#000000'/>
		</definition>
		<application>
			<apply toObject='caption' styles='caption' />
			<apply toObject='subcaption' styles='subcaption' />
			<apply toObject='XAXISNAME' styles='xaxis' />
			<apply toObject='YAXISNAME' styles='yaxis' />
			<apply toObject='DATALABELS' styles="labels"/>
			<apply toObject='Legend' styles="LegendFont"/>
			<apply toObject='TRENDVALUES' styles="labels2"/> 
		</application>
	</styles>
</chart>	