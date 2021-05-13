import React, {useEffect, useRef, useCallback} from 'react';
import RNCallKeep from 'react-native-callkeep';
import {Notifications, Registered} from 'react-native-notifications';

type Props = {};

type Call = {
  callId: string;
  roomName: string;
  callerName: string;
  enableVideo: boolean;
  roomSid: string;
};
const CallKeepManager: React.FC<Props> = () => {
  const currentCall = useRef<Call>();
  const pendingAction = useRef<string>();

  const initializeCallKeep = async () => {
    Notifications.registerRemoteNotifications();
    Notifications.events().registerRemoteNotificationsRegistered(
      (event: Registered) => {
        console.log(event.deviceToken);
      },
    );

    try {
      await RNCallKeep.setup({
        ios: {
          appName: 'RN call',
          maximumCallGroups: '1',
          maximumCallsPerCallGroup: '1',
          supportsVideo: true,
          imageName: 'inCallIcon',
        },
        android: {
          alertTitle: 'Permissions required',
          alertDescription:
            'This application needs to access your phone accounts',
          cancelButton: 'Cancel',
          okButton: 'OK',
          imageName: 'iconmask',
          selfManaged: true,
          foregroundService: {
            channelId: 'APP_CALL_SERVICE',
            channelName: 'Call service for the Call app',
            notificationTitle: 'RNCALL ongoing call',
            notificationIcon: 'ic_launcher',
          },
          additionalPermissions: [],
        },
      });
      RNCallKeep.setAvailable(true);
    } catch (err) {
      console.error('initializeCallKeep error:', err.message);
    }

    RNCallKeep.setForegroundServiceSettings({
      channelId: 'APP_CALL_SERVICE',
      channelName: 'Foreground service for my app',
      notificationTitle: 'My app is running on background',
      notificationIcon: 'Path to the resource icon of the notification',
    });

    // Add RNCallKit Events
    RNCallKeep.addEventListener('didReceiveStartCallAction', e =>
      console.log(e),
    );
    RNCallKeep.addEventListener('answerCall', e => console.log(e));
    RNCallKeep.addEventListener('endCall', e => console.log(e));
    RNCallKeep.addEventListener('didDisplayIncomingCall', e => console.log(e));
    RNCallKeep.addEventListener('didPerformSetMutedCallAction', e =>
      console.log(e),
    );
    RNCallKeep.addEventListener('didPerformDTMFAction', e => console.log(e));
  };

  useEffect(() => {
    initializeCallKeep();
  }, []);
  return <></>;
};

export default CallKeepManager;
