<jsp:directive.page import="com.ideaweavers.exportsys.ReportExport"/>
<jsp:directive.page import="com.ideaweavers.exportsys.CSVWriter"/>
<jsp:directive.page import="com.ideaweavers.exportsys.IIWExporter"/><%@ include file="iw_page_setup.jspf" %>
<%
//setup Business Object information
String sPageLabel = "Sprint Monthly Repair Level Report";
pageContext.setAttribute("label", sPageLabel);

String sFileExport = IWUtils.parseRString("FileExport", request);

/*
//Security Check
if (!SecMgr.checkPerms(iPersonID, Tables.i_Tables, lBusObj.getIWBusObjID(), Perms.READ.intValue())) {
    throw new IWPageException(ErrorLevel.Security, "Access Denied", "You do not have permission to view the " + lBusObj.DisplayName + " data.");
}*/

String sAction = IWUtils.parseString(request.getParameter("action"));
boolean userSearched=false;//whether or not the user pressed the 'search key'
if(sAction.length() > 0) {
    userSearched=true;
}else {
    sAction = "Search";
}

//Previously Entered Criteria
String sWhere="";

String  sModelNumber = IWUtils.parseString(request.getParameter("qModelNumber"));
if(sModelNumber.length() > 0){
    sWhere += " AND Model LIKE '" +sModelNumber + "%' ";
}

// PAGE SETUP OBJECT NAME AND ID NUMBER
pageContext.setAttribute("TableID", Tables.Report);
sActiveSubmenu = "reports";

Connection conn = null;
PreparedStatement psModel = null;
PreparedStatement psShipMonth = null;
PreparedStatement psAll = null;
PreparedStatement psAllModel = null;
PreparedStatement psTotal = null;
ResultSet lRes = null, lModelRS = null;
ResultSet lRSsm = null, lSMRS = null;
ResultSet lRSAll = null;
ResultSet lRSTotal = null;

int iYear =  IWUtils.parseIntString(request.getParameter("Year"), IWUtils.generateDisplayDate(IWUtils.Now(), "yyyy"));
String sMonth =  IWUtils.parseString(request.getParameter("Month"), IWUtils.generateDisplayDate(IWUtils.Now(), "mm"));

try {
    String sAllModel = " SELECT model FROM `pcdmonthlysummary` WHERE `servicecenterid` = '7676' " + sWhere + "  GROUP BY model; ";
    String sAllMonths = " SELECT shipmonth FROM `pcdmonthlysummary` GROUP BY shipmonth ORDER BY shipmonth ";

    String sModelQuery = " SELECT Qty FROM `pcdmonthlysummary` WHERE `servicecenterid` = '7676' AND `Model` = ? AND `ShipMonth` = ? AND `level` = ?; ";
//    String sAllModelQuery = " SELECT Qty FROM `pcdmonthlysummary` WHERE `servicecenterid` = '7676' AND `Model` = ? AND `ShipMonth` = ? AND `level` = ?; ";
    //    1st by primary

    String sMonthTotal = " SELECT SUM(Qty) AS MonthTotal FROM `pcdmonthlysummary` WHERE `servicecenterid` = '7676' AND`ShipMonth` = ? AND  `Model` = ?  ";
    //String sAllShip = " SELECT shipmonth, SUM(Qty) AS MonthTotal FROM `pcdmonthlysummary` WHERE `servicecenterid` = '7676' AND `model` = ? GROUP BY shipmonth; ";

%>
<%@ include file="iw_page_begin.jspf" %>

<h2 class="title"><img class="icon" src="imgs/icons/<%=lBusObj.TableName%>.gif" /><%=sPageLabel%></h2>

<div id="QueryTool" class="query_tool">
    <form action="main_rpt_sprint_monthly.jsp" name="fmain" method="get" id="main_sro">
    <input type="hidden" name="action" value="<%=sAction%>">
        <div class="header">
            <span class="right">
                 <input type="submit" value="Search" onClick="javascript:document.fmain.action.value='Search';document.fmain.submit();">
            </span>
            <a class="header_title">Query Tool - Analysis Source</a>
        </div>
        <table>
            <tr>
                <td class='label'>Model:</td>
                <td><input type=text name='qModelNumber' id='qModelNumber' value='<%=sModelNumber%>'></td>
                <td class="label">Export to File:</td>
                <td class="input">
                    <select id="FileExport" name="FileExport">
                        <option value="">None</option>
                        <option value="XLS" <%=(sFileExport.equalsIgnoreCase("XLS")?"selected":"") %>>Excel</option>
                        <option value="CSV" <%=(sFileExport.equalsIgnoreCase("CSV")?"selected":"") %>>CSV</option>
                    </select>
                </td>
            </tr>
        </table>
    </form>
</div>
<%    boolean bExport = false;
    IIWExporter theExporter = null;
    String sFileName = "SprintMonthlyReport_" +  sModelNumber;
    int iARowCount = 0; %>

<%if(userSearched){
    if(conn == null){
        conn = DBManager.getInstance().getConnection();
    }
//NOW EXECUTE QUERY
    psAllModel = conn.prepareStatement(sAllModel);
    psShipMonth = conn.prepareStatement(sAllMonths);
    lRSsm = psShipMonth.executeQuery();
 %>
<%int colCount = 0;%>
    <div id="ResTable">
    <%    ArrayList<String> alHeader = new ArrayList<String>(); //to dynamically store the headers and for the sHeaders string array
        psTotal = conn.prepareStatement(sMonthTotal);
        StringBuffer sbHeader = new StringBuffer();
        sbHeader.append("<tr>");
            sbHeader.append("<th>Level</th>");
            alHeader.add(0, "Level");
            int iMonthCount = 1;
            while(lRSsm.next()){
                alHeader.add(iMonthCount, IWUtils.generateDisplayDate(IWUtils.parseTimestampString(lRSsm.getString("shipmonth"), "yyyyMM"), "MMM yy"));
                sbHeader.append("<th>"+ alHeader.get(iMonthCount) + "</th>");
                alHeader.add(iMonthCount+1,  "%");
                 sbHeader.append("<th>" + alHeader.get(iMonthCount+1) + "</th>");
                colCount++;
                iMonthCount = iMonthCount + 2;
             }//while
            alHeader.add(iMonthCount, "Totals");
            sbHeader.append("<th>" + alHeader.get(iMonthCount) + "</th>");
            lRSsm.beforeFirst();
            sbHeader.append("</tr>");
            System.out.println("iMonthCount is " + iMonthCount);
            String[] sHeaders = new String[iMonthCount + 1]; //Added +1 because iMonthCount started at 1
            for(int h = 0 ; h < iMonthCount + 1; h++){
                sHeaders[h] = alHeader.get(h);
                //System.out.println("At index " + h + " sHeaders contents: " + sHeaders[h]);
            }//for

            int iResCnt = 0;
            String sCurrentMod = "Start Blank";
            int iFlag = 0;
            String[] sLevelList = new String[]{"Warr", "OOW", "BER", "DBR", "NTF True", "NTF Cosmetics", "None Specified"};
            ArrayList<Object> aRow = new ArrayList<Object>();

            //Initializing XLS/CSV formats for theExporter
            if(sFileExport.equalsIgnoreCase("XLS")){
                bExport = true;
                int[] aTypes = new int[sHeaders.length];
                for(int i = 0; i < sHeaders.length; i++ ){
                    aTypes[i] =  ReportExport.STRING_TYPE;
                }//for establishing header types
                theExporter = new ReportExport(sHeaders, aTypes, this.getServletContext().getRealPath("/") + "excel//", sFileName);
            }else     if(sFileExport.equalsIgnoreCase("CSV")){
                bExport = true;
                theExporter = new CSVWriter(this.getServletContext().getRealPath("/") + "csv//", sFileName);
            }

        lRSAll = psAllModel.executeQuery();
        %>
    <%while(lRSAll.next()){
        sModelNumber = lRSAll.getString("Model");

        int ntfArray[] = new int[12];
        int ntfCosArray[] = new int[12];
        ArrayList<Integer> alMonthTotals = new ArrayList<Integer>();
        int x=0;
        while(lRSsm.next()){
            psTotal.setString(1, lRSsm.getString("ShipMonth"));
            psTotal.setString(2, sModelNumber);
            lRSTotal = psTotal.executeQuery();
            if(lRSTotal.next()){
                alMonthTotals.add(x, lRSTotal.getInt("MonthTotal"));
            }else{
                alMonthTotals.add(x, 0);
            }
            x++;
        }
        int [] iMonthTotals = new int[alMonthTotals.size()];
        for(int i = 0; i < alMonthTotals.size(); i++){
            iMonthTotals[i] = alMonthTotals.get(i);
        }
        lRSsm.beforeFirst();%>
    <table class="results" id="resultsTable">
        <thead>
            <tr>
                <th bgcolor=#ff3333 colSpan=100%><b>Model:</b><%=sModelNumber %></th>
            </tr>
            <%=sbHeader.toString() %>
            <%//write header to CSV/Excel
            if(bExport){
                if(sFileExport.equalsIgnoreCase("CSV")){
                    theExporter.addRow(new ArrayList<Object>(Arrays.asList("Model: " + sModelNumber)));
                    theExporter.addRow(new ArrayList<Object>(Arrays.asList(sHeaders)));
                }
                aRow = new ArrayList<Object>();
                }%>
        </thead>
<%//RESULT SETS %>
        <tbody>
    <% psModel = conn.prepareStatement(sModelQuery);
        int[] levelCounts = new int[12];
        int thisDate = (iYear * 100) + Integer.parseInt(sMonth);
        for(int rowNum = 0; rowNum < sLevelList.length; rowNum++){
            String sCurrentLevel = sLevelList[rowNum];%>
            <tr onMouseOver="this.style.background='#efefef';" onMouseOut="this.style.background='white';">
            <%---First cell: level name --%>
                <td><%=sCurrentLevel%></td>
                <%
                aRow.add(sCurrentLevel);
                int iColumnIndex = 0;
                %>
            <%---Second cell: total and percent total for first month --%>
                <%while(lRSsm.next()){
                    String sShipMonth = IWUtils.readString(lRSsm, "shipmonth");
                    psModel.setString(1, sModelNumber);
                    psModel.setString(2, sShipMonth);
                    if(sCurrentLevel.equalsIgnoreCase("None Specified")){
                        psModel.setString(3,"");
                    } else{
                        psModel.setString(3,sCurrentLevel);
                        }
                    System.out.println("psModel: " + psModel.toString());
                    lRes = psModel.executeQuery();

                    int tally = 0;
                    if(lRes.next()){
                        tally = lRes.getInt("Qty");
                        levelCounts[rowNum] += tally;
                        if(rowNum == 4){
                            ntfArray[iColumnIndex] += tally;
                        } else if(rowNum == 5){
                            ntfCosArray[iColumnIndex] += tally;
                        }
                    }//if.lREs.next
                     aRow.add(IWUtils.generateDisplayDouble(tally, "###,##0"));
                     String sPercent = "";
                     if(tally > 0){
                        sPercent = IWUtils.generateDisplayDouble(((tally*1.0)/iMonthTotals[iColumnIndex])*100., "###,##0.0");
                    } else {
                        sPercent = "0.0";
                        }
                     %>
                    <td><%=tally%></td>
                    <td><%=sPercent%>%</td>
                    <% aRow.add(sPercent);
                    System.out.println("Tally for row " + rowNum + " is " + tally);
                    iColumnIndex++;
                }//while lRSsm
                lRSsm.beforeFirst(); %>
        <%---Last cell: total of month totals --%>
                    <td colSpan = 2>
                        <%=levelCounts[rowNum]%>
 <%aRow.add(IWUtils.generateDisplayDouble(levelCounts[rowNum], "###,##0"));%>
                    </td>
                </tr><%//end result row creation %>
        <%---End level row --%>
                <%if(bExport){
                    theExporter.addRow(aRow);
                    aRow =  new ArrayList<Object>();
                    }
                 System.out.println("Finished writing first row.");
        }//while rowNum < sLevelList.length %>
            <tr>
                <td colspan=<%=(colCount*2)+2%>><hr></td>
            </tr>
            <%if(sFileExport.equalsIgnoreCase("XLS")){
            for(int h = 0; h < iMonthTotals.length *2 +2; h++){
                aRow.add("");
                }
            }

            if(bExport){
                theExporter.addRow(aRow);
                aRow =  new ArrayList<Object>();
                }
            if(sFileExport.equalsIgnoreCase("XLS")){
            for(int h = 0; h < iMonthTotals.length * 2 + 2; h++){
                aRow.add("");
                }
            }
            if(bExport){
                theExporter.addRow(aRow);
                aRow =  new ArrayList<Object>();
            }%>
        <%---Start Monthly Totals Shipped --%>
            <tr >
                <td><b>Sprint Monthly Request</b></td>
            </tr>
            <%aRow.add("Sprint Monthly Request");
            if(sFileExport.equalsIgnoreCase("XLS")){
            for(int h = 0; h < iMonthTotals.length * 2 + 2; h++){
                aRow.add("");
                }
            }
            if(bExport){
                theExporter.addRow(aRow);
                aRow =  new ArrayList<Object>();
            }  %>
            <tr bgcolor=#ffff99>
                <td><b>Total Shipped:</b></td>
                <%aRow.add("Total Shipped:");
                 lRSsm.beforeFirst();

                for(int k = 0; k < iMonthTotals.length; k++){%>
                    <td colSpan = 2><b><%=iMonthTotals[k]%></b></td>
                    <% aRow.add(IWUtils.generateDisplayDouble(iMonthTotals[k], "###,##0"));
                    if(sFileExport.equalsIgnoreCase("XLS")){
                        aRow.add("");
                    }//if
                }//for

                 int iFullTotal = 0; %>
                <td colSpan = 2>
                    <%for(int p = 0; p < iMonthTotals.length; p++){
                        iFullTotal += levelCounts[p];
                    }%>
                    <b><%=iFullTotal%></b>
 <%aRow.add(IWUtils.generateDisplayDouble(iFullTotal, "###,##0")); %>
                </td>
            </tr>
            <% if(bExport){
                    theExporter.addRow(aRow);
                    aRow =  new ArrayList<Object>();
                }  %>
        <%---Start Shipped to Request --%>
            <tr>
                <td bgcolor=#CCFFCC>
                <b>% Shipped to Request</b>
                <%aRow.add("% Shipped to Request"); %>
                </td>
                <%for(int h = 0; h < iMonthTotals.length; h++){%>
                    <td colSpan=2><b>%</b></td>
                    <%aRow.add("%");
                    if(sFileExport.equalsIgnoreCase("XLS")){
                        aRow.add("");
                    }//if
                }//for%>
            </tr>
            <%if(sFileExport.equalsIgnoreCase("XLS")){
                aRow.add("");//placeholder for total
            }//if
            if(bExport){
                theExporter.addRow(aRow);
                aRow =  new ArrayList<Object>();
            }%>
            <tr>
                <td colspan=<%=(colCount*2)+2%>><hr></td>
            </tr>
            <%if(sFileExport.equalsIgnoreCase("XLS")){
            for(int h = 0; h < iMonthTotals.length * 2 + 2; h++){
                aRow.add("");
                }
            }
            if(bExport){
                theExporter.addRow(aRow);
                aRow =  new ArrayList<Object>();
            }%>
        <%---Start NTF Totals --%>
            <tr>
                <td><b>NTF</b></td>
                <%aRow.add("NTF"); %>
                <%for(int h = 0; h< colCount; h++){%>
                    <td colSpan=2>
                        <%= ntfArray[h]%>
 <%aRow.add(IWUtils.generateDisplayDouble(ntfArray[h], "###,##0"));
                        if(sFileExport.equalsIgnoreCase("XLS")){
                            aRow.add("");
                        }//if%>
                    </td>
                <%}%>
                <td colSpan = 2>
 <%=IWUtils.generateDisplayDouble(levelCounts[4], "###,##0") %>
 <%aRow.add(IWUtils.generateDisplayDouble(levelCounts[4], "###,##0"));%>
                </td>
            </tr>
            <% if(bExport){
                    theExporter.addRow(aRow);
                    aRow =  new ArrayList<Object>();
                }  %>
        <%---Start NTF Cosmetics --%>
            <tr>
                <td bgcolor=#00Bfff><b>NTF w/Cosmetics</b></td>
                <%aRow.add("NTF w/Cosmetics"); %>
                <%for(int h = 0; h < colCount; h++){%>
                    <td colSpan=2><%= ntfCosArray[h]%></td>
 <%aRow.add(IWUtils.generateDisplayDouble(ntfCosArray[h], "###,##0"));
                    if(sFileExport.equalsIgnoreCase("XLS")){
                        aRow.add("");
                    }//if%>
                <%}%>

                <td colSpan = 2>
                    <%=levelCounts[5]%>
 <%aRow.add(IWUtils.generateDisplayDouble(levelCounts[5], "###,##0")); %>
                </td>
            </tr>
            <%if(bExport){
                    theExporter.addRow(aRow);
                    aRow =  new ArrayList<Object>();
                    }%>
            <tr>
                <td colspan=<%=(colCount*2)+2%>><hr></td>
            </tr>
        <%---Start NTF Percents --%>
            <tr>
                <td><b>% NTF</b></td>
                <%aRow.add("% NTF"); %>
                <%String sPercent = "";
                for(int h = 0; h< colCount; h++){
                    if(ntfArray[h]>0){
                        sPercent = IWUtils.generateDisplayDouble(((ntfArray[h]*1.0)/iMonthTotals[h])*100., "###,##0.0");
                    } else {
                        sPercent = "0.0";
                    }//else
                    aRow.add(sPercent);
                    if(sFileExport.equalsIgnoreCase("XLS")){
                        aRow.add("");
                    }//if%>
                    <td colSpan=2><%=sPercent%>%</td>
                <%}//for%>
                <td colSpan = 2>
 <%=IWUtils.generateDisplayDouble(((levelCounts[4]*1.0)/iFullTotal)*100., "###,##0.0")%>%
 <%aRow.add(IWUtils.generateDisplayDouble(((levelCounts[4]*1.0)/iFullTotal)*100., "###,##0.0") + "%"); %>
                </td>
            </tr>
            <% if(bExport){
                    theExporter.addRow(aRow);
                    aRow =  new ArrayList<Object>();
                }  %>
            <tr>
                <td bgcolor=##64fe2e><b>% NTF w/o Cos</b></td>
                <%aRow.add("% NTF w/o Cos");
                String sNtfwoCosPercent = "";
                for(int h = 0; h< colCount; h++){
                    if(ntfCosArray[h] > 0){
                        sNtfwoCosPercent = IWUtils.generateDisplayDouble(((ntfCosArray[h]*1.0)/iMonthTotals[h])*100., "###,##0.0");
                    }    else{
                        sNtfwoCosPercent = "0.0";
                    }
                    aRow.add(sNtfwoCosPercent);
                    if(sFileExport.equalsIgnoreCase("XLS")){
                        aRow.add("");
                    }//if%>
                    <td colSpan=2><%=sNtfwoCosPercent%>%</td>
                <%}//for%>
                <td colSpan=2>
 <%=IWUtils.generateDisplayDouble(((levelCounts[5]*1.0)/iFullTotal)*100., "###,##0.0")%>%
 <%aRow.add(IWUtils.generateDisplayDouble(((levelCounts[5]*1.0)/iFullTotal)*100., "###,##0.0")); %>
                </td>
            </tr>
            <% if(bExport){
                    theExporter.addRow(aRow);
                    aRow =  new ArrayList<Object>();
                }  %>
            <tr>
                <td><b>% NTF Combined</b></td>
                <%aRow.add("NTF Combined");
                String sNtfComboPercent = "";
                for(int h = 0; h< colCount; h++){
                    if(ntfArray[h]+ntfCosArray[h] > 0){
                        sNtfComboPercent = IWUtils.generateDisplayDouble((((ntfArray[h]+ntfCosArray[h])*1.0)/iMonthTotals[h])*100., "###,##0.0");
                    } else {
                        sNtfComboPercent = "0.0";
                    }%>
                    <td colSpan = 2><%=sNtfComboPercent%>%</td>
                    <%aRow.add(sNtfComboPercent);
                    if(sFileExport.equalsIgnoreCase("XLS")){
                        aRow.add("");
                    }//if%>
                <%}%>
                <td colSpan=2>
 <%=IWUtils.generateDisplayDouble((((levelCounts[4]+levelCounts[5])*1.0)/iFullTotal)*100., "###,##0.0")%>%
 <%aRow.add(IWUtils.generateDisplayDouble((((levelCounts[4]+levelCounts[5])*1.0)/iFullTotal)*100., "###,##0.0") + "%");
                if(bExport){
                    if(sFileExport.equalsIgnoreCase("CSV")){
                        aRow.add("\r\n\r\n");
                        }
                    } %>
                </td>
            </tr>
            <% if(bExport){
                    theExporter.addRow(aRow);
                }  %>
            <tr>
                <td colspan=<%=(colCount*2)+2%>><hr></td>
            </tr>
            <%if(sFileExport.equalsIgnoreCase("XLS")){
            for(int h = 0; h < iMonthTotals.length * 2 + 2; h++){
                aRow.add("");
                }
            }
            if(bExport){
                theExporter.addRow(aRow);
                aRow =  new ArrayList<Object>();
            }%>
    <%}//Model Result Set

    if(bExport){
        theExporter.writeAll();%>
        <tr bgcolor = yellow>
            <td colspan = 100%>
                <div class=report><%= theExporter.generateFileLink()%></div>
            </td>
        </tr>
        <%theExporter.close(); %>
    <% } %>
     </tbody>
     </table>
    <%}else { //usersearched%>
    <b>Please Click 'Search' to return <%=lBusObj.DisplayMultipleName%></b>
    <%} %>

</div>
<%}finally{ //try
       DBManager.close(lRes);
    DBManager.close(null, psModel, lRes);
    DBManager.close(null, psShipMonth, lSMRS);
}%>
<%@ include file="iw_page_end.jspf" %>
<%@ include file="iw_free_db_conn.jspf" %>
