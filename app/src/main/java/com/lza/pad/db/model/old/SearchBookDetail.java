package com.lza.pad.db.model.old;

import java.util.ArrayList;

public class SearchBookDetail
{
    private String returnnum;
    private String status;
    private ArrayList<PingLun> pinglunList;
    private String cnt1;
    private String cnt2;
    private String tubiao;
    private String pinglunNumber;
    private String detailaus;
    private String detailcnts;
    private String detailms;

    public String getPinglunNumber()
    {
        return pinglunNumber;
    }

    public void setPinglunNumber(String pinglunNumber)
    {
        this.pinglunNumber = pinglunNumber;
    }

    public String getReturnnum()
    {
        return returnnum;
    }

    public void setReturnnum(String returnnum)
    {
        this.returnnum = returnnum;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public ArrayList<PingLun> getPinglunList()
    {
        return pinglunList;
    }

    public void setPinglunList(ArrayList<PingLun> pinglunList)
    {
        this.pinglunList = pinglunList;
    }

    public String getCnt1()
    {
        return cnt1;
    }

    public void setCnt1(String cnt1)
    {
        this.cnt1 = cnt1;
    }

    public String getCnt2()
    {
        return cnt2;
    }

    public void setCnt2(String cnt2)
    {
        this.cnt2 = cnt2;
    }

    public String getTubiao()
    {
        return tubiao;
    }

    public void setTubiao(String tubiao)
    {
        this.tubiao = tubiao;
    }

	public String getDetailaus() {
		return detailaus;
	}

	public void setDetailaus(String detailaus) {
		this.detailaus = detailaus;
	}

	public String getDetailcnts() {
		return detailcnts;
	}

	public void setDetailcnts(String detailcnts) {
		this.detailcnts = detailcnts;
	}

	public String getDetailms() {
		return detailms;
	}

	public void setDetailms(String detailms) {
		this.detailms = detailms;
	}
    
}
