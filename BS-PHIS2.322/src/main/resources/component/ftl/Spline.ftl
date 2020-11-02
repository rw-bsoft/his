<?xml version="1.0" encoding="gb2312"?>
<#assign group=headers[0]>
<chart caption="${title}" subcaption="${subTitle}" use3DLighting="1" lineThickness="2"  anchorBorderThickness="0.1" numberScaleValue="10000,10000" numberScaleUnit="万,亿" labelDisplay="none" xAxisName='${group.alias}'  yAxisName='' showValues='1' yAxisMinValue='40' legendPosition ="RIGHT">
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
			<dataset seriesName='${h.alias}'>
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
			<style name='labels' type='font' size='10' color='#000000'/>
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