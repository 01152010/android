package com.core.enumeration;

public enum CmdType {
    ControlScene,
    ControlCurtain,
    ControlLock,
    ControlHWAC,
    Control485AC,
    ControlTV,
    ControlLight,
    ControlQD,
    ControlService,
    QuaryDeviceList,
    QuaryDeviceStatus,
    QuaryDeviceListStatus,
    GetRoomInfo,
    ControlBBGroup,

    ReportBBControl,
    ReportBBKTStatus,
    ReportBBNodeStatus,

    ReportCCVersionUpdate,
    PushVersionUpdateStatus,
    ReportNodeStatus,
    ReportDeviceList,
    ReportKTGroup,
    CloudControlUpad,
    PushVersion,
    CloudQuaryVersion,

    CommonAck
}
