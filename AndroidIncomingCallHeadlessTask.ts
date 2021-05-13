// @flow
import CallNotifications from './CallNotifications';

type CallData = {
  callUuid: string;
  roomName: string;
  callerName: string;
  roomSid: string;
  enableVideo: boolean;
};

export default ({
  callUuid,
  roomName,
  callerName,
  roomSid,
  enableVideo,
}: CallData) => {
  CallNotifications.showIncomingCallNotification(
    callUuid,
    roomName,
    callerName,
    roomSid,
    enableVideo,
  );

  return Promise.resolve();
};
