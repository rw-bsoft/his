<?xml version="1.0" encoding="gb2312"?>
<#assign group=headers[0]>
<chart caption="${title}" anchorradius='4' canvasBorderThickness="2" canvasBorderAlpha="60" showBorder="0" subcaption="${subTitle}" useRoundEdges='1' numVDivLines='10' use3DLighting="1" numberScaleValue="10000,10000" baseFontSize='20' labelDisplay="STAGGER" numberScaleUnit="万,亿" labelDisplay="none"  yAxisName='' showValues='1' legendPosition ="RIGHT">
	<categories>
	<#list rs as r>
		<#if r_index = limit>
			<#break>
		</#if>
		<#if group.dic??>
			<category label='${r[group.id + "_text"]?replace('卫生局', '' , '')?replace('社区卫生服务中心', '' , '')!""}'/>
		<#else>
			<category label='${r[group.id]!""}'/>
		</#if>
	</#list>
	</categories>
	<#list headers as h>
		<#if h.func??>
			<dataset seriesName='${h.alias?replace('卫生局', '' , '')?replace('社区卫生服务中心', '' , '')}'>
			<#list rs as r>
				<#if r_index = limit>
					<#break>
				</#if>
				<#if (r[h.id]!"")?length gt 0>
				<set value='${r[h.id]}'
				<#if diggers??>
					link="javascript:FC_Click('${chartId}',${r_index},
					<#if diggers['match']??>
						'${diggers.match}'
					<#else>
						<#if diggers[h.id]??>
							'${h.id}'
						<#else>
							'${group.id}'
						</#if>
					</#if>
					)"
				</#if>
				/>	
				</#if>
			</#list>
			</dataset>
		</#if>
	</#list>
	<styles>
		<definition>
			<style name='caption' type='font' size='28' color='666666'/>
			<style name='subcaption' type='font' size='20' color='666666' bold='0'/>
			<style name='yaxis' type='font' font='@宋体' size='20' bold='0' />
			<style name='xaxis' type='font' size='20' bold='0' />
			<style name='labels' type='font' size='20' color='#000000'/>
		</definition>
		<application>
		<apply toObject='canvas' styles='caption' />
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