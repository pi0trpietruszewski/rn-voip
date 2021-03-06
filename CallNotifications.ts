import {NativeModules, Platform} from 'react-native';

const {AppCallNotifications} = NativeModules;

interface CallNotificationsInterface {
  broadcastCallStarted(callUuid: string, name: string): void;
  broadcastCallEnded(callUuid: string): void;
  reportIncomingCallCancelled(callUuid: string): void;
  answerCall(callUuid: string): void;
  rejectCall(callUuid: string): void;
  showIncomingCallNotification(
    callUuid: string,
    roomName: string,
    callerName: string,
    roomSid: string,
    enableVideo: boolean,
  ): Promise<string>;
  launchIncomingCallActivity(): void;
  finishIncomingCallActivity(): void;
  launchMainActivity(): void;
}

const callNotifications = Platform.select({
  android: AppCallNotifications,
  default: {
    broadcastCallStarted: () => {},
    broadcastCallEnded: () => {},
    reportIncomingCallCancelled: () => {},
    answerCall: () => {},
    rejectCall: () => {},
    showIncomingCallNotification: () => Promise.resolve(),
    launchIncomingCallActivity: () => {},
    finishIncomingCallActivity: () => {},
    launchMainActivity: () => {},
  },
}) as CallNotificationsInterface;

export default callNotifications;
