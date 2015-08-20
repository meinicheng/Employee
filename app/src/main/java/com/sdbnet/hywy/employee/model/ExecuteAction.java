package com.sdbnet.hywy.employee.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.sdbnet.hywy.employee.album.AlbumHelper.ImageItem;

public class ExecuteAction implements Serializable {

	private static final long serialVersionUID = 6499019604661603285L;

	// private List<String> actidxs;//动作序号集合
	private String actidx; // 动作序号
	private String action; // 动作码
	private String actname; // 动作名称
	private String actmemo; // 动作说明
	private String lineno; // 轨迹编号
	private String linename; // 轨迹名称
	private String sign; // 是否更新到订单状态
	private String btnname; // 扫描按钮名称
	private String workflow; // 工作流
	private String startnode; // 是否为起始节点
	private String iscall; // 呼叫权限
	private String islocate; // 定位权限
	private String isscan; // 扫描权限
	private String showinner; // 是否内部显示
	private String actmemoinner=""; // 动作执行备注
	//
	private String actTime;// 定单时间;
	// private ArrayList<String> actImgList;// 定单ImgList;
	// private String imsgs;// 图片集合
	private ArrayList<ImageItem> mImageItems=new ArrayList<>();

	public String getActmemoinner() {
		return actmemoinner == null ? "" : actmemoinner;
	}

	public void setActmemoinner(String actmemoinner) {
		this.actmemoinner = actmemoinner;
	}

	public String getShowinner() {
		return showinner;
	}

	public void setShowinner(String showinner) {
		this.showinner = showinner;
	}

	public String getIscall() {
		return iscall;
	}

	public void setIscall(String iscall) {
		this.iscall = iscall;
	}

	public String getIslocate() {
		return islocate;
	}

	public void setIslocate(String islocate) {
		this.islocate = islocate;
	}

	public String getIsscan() {
		return isscan;
	}

	public void setIsscan(String isscan) {
		this.isscan = isscan;
	}

	public String getBtnname() {
		return btnname;
	}

	public void setBtnname(String btnname) {
		this.btnname = btnname;
	}

	public String getWorkflow() {
		return workflow;
	}

	public void setWorkflow(String workflow) {
		this.workflow = workflow;
	}

	public String getStartnode() {
		return startnode;
	}

	public void setStartnode(String startnode) {
		this.startnode = startnode;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getActype() {
		return actype;
	}

	public void setActype(String actype) {
		this.actype = actype;
	}

	private String actype;

	public String getActidx() {
		return actidx;
	}

	public void setActidx(String actidx) {
		this.actidx = actidx;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getActname() {
		return actname;
	}

	public void setActname(String actname) {
		this.actname = actname;
	}

	public String getActmemo() {
		return actmemo;
	}

	public void setActmemo(String actmemo) {
		this.actmemo = actmemo;
	}

	public String getLineno() {
		return lineno;
	}

	public void setLineno(String lineno) {
		this.lineno = lineno;
	}

	public String getLinename() {
		return linename;
	}

	public void setLinename(String linename) {
		this.linename = linename;
	}

	public String getActTime() {
		return actTime;
	}

	public void setActTime(String actTime) {
		this.actTime = actTime;
	}

	public ArrayList<ImageItem> getImageItems() {
		if(mImageItems==null)
			mImageItems=new ArrayList<>();
		return mImageItems;
	}

	public void setImageItems(ArrayList<ImageItem> mImageItems) {
		this.mImageItems = mImageItems;
	}

	// public String getImsgs() {
	// return imsgs;
	// }
	//
	// public void setImsgs(String imsgs) {
	// this.imsgs = imsgs;
	// }

	@Override
	public String toString() {
		return "ExecuteAction [actidx=" + actidx + ", action=" + action
				+ ", actname=" + actname + ", actmemo=" + actmemo + ", lineno="
				+ lineno + ", linename=" + linename + ", sign=" + sign
				+ ", btnname=" + btnname + ", workflow=" + workflow
				+ ", startnode=" + startnode + ", iscall=" + iscall
				+ ", islocate=" + islocate + ", isscan=" + isscan
				+ ", showinner=" + showinner + ", actmemoinner=" + actmemoinner
				+ ", actTime=" + actTime + ", mImageItems=" + mImageItems
				+ ", actype=" + actype + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actTime == null) ? 0 : actTime.hashCode());
		result = prime * result + ((actidx == null) ? 0 : actidx.hashCode());
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((actmemo == null) ? 0 : actmemo.hashCode());
		result = prime * result
				+ ((actmemoinner == null) ? 0 : actmemoinner.hashCode());
		result = prime * result + ((actname == null) ? 0 : actname.hashCode());
		result = prime * result + ((actype == null) ? 0 : actype.hashCode());
		result = prime * result + ((btnname == null) ? 0 : btnname.hashCode());
		result = prime * result + ((iscall == null) ? 0 : iscall.hashCode());
		result = prime * result
				+ ((islocate == null) ? 0 : islocate.hashCode());
		result = prime * result + ((isscan == null) ? 0 : isscan.hashCode());
		result = prime * result
				+ ((linename == null) ? 0 : linename.hashCode());
		result = prime * result + ((lineno == null) ? 0 : lineno.hashCode());
		result = prime * result
				+ ((mImageItems == null) ? 0 : mImageItems.hashCode());
		result = prime * result
				+ ((showinner == null) ? 0 : showinner.hashCode());
		result = prime * result + ((sign == null) ? 0 : sign.hashCode());
		result = prime * result
				+ ((startnode == null) ? 0 : startnode.hashCode());
		result = prime * result
				+ ((workflow == null) ? 0 : workflow.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExecuteAction other = (ExecuteAction) obj;
		if (actTime == null) {
			if (other.actTime != null)
				return false;
		} else if (!actTime.equals(other.actTime))
			return false;
		if (actidx == null) {
			if (other.actidx != null)
				return false;
		} else if (!actidx.equals(other.actidx))
			return false;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (actmemo == null) {
			if (other.actmemo != null)
				return false;
		} else if (!actmemo.equals(other.actmemo))
			return false;
		if (actmemoinner == null) {
			if (other.actmemoinner != null)
				return false;
		} else if (!actmemoinner.equals(other.actmemoinner))
			return false;
		if (actname == null) {
			if (other.actname != null)
				return false;
		} else if (!actname.equals(other.actname))
			return false;
		if (actype == null) {
			if (other.actype != null)
				return false;
		} else if (!actype.equals(other.actype))
			return false;
		if (btnname == null) {
			if (other.btnname != null)
				return false;
		} else if (!btnname.equals(other.btnname))
			return false;
		if (iscall == null) {
			if (other.iscall != null)
				return false;
		} else if (!iscall.equals(other.iscall))
			return false;
		if (islocate == null) {
			if (other.islocate != null)
				return false;
		} else if (!islocate.equals(other.islocate))
			return false;
		if (isscan == null) {
			if (other.isscan != null)
				return false;
		} else if (!isscan.equals(other.isscan))
			return false;
		if (linename == null) {
			if (other.linename != null)
				return false;
		} else if (!linename.equals(other.linename))
			return false;
		if (lineno == null) {
			if (other.lineno != null)
				return false;
		} else if (!lineno.equals(other.lineno))
			return false;
		if (mImageItems == null) {
			if (other.mImageItems != null)
				return false;
		} else if (!mImageItems.equals(other.mImageItems))
			return false;
		if (showinner == null) {
			if (other.showinner != null)
				return false;
		} else if (!showinner.equals(other.showinner))
			return false;
		if (sign == null) {
			if (other.sign != null)
				return false;
		} else if (!sign.equals(other.sign))
			return false;
		if (startnode == null) {
			if (other.startnode != null)
				return false;
		} else if (!startnode.equals(other.startnode))
			return false;
		if (workflow == null) {
			if (other.workflow != null)
				return false;
		} else if (!workflow.equals(other.workflow))
			return false;
		return true;
	}

}
