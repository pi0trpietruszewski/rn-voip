import React from 'react';
import {View, Text, StyleSheet} from 'react-native';
import {TouchableOpacity} from 'react-native-gesture-handler';

type Props = {
  route: {
    params?: {
      callUuid: string;
      roomName: string;
      username: string;
      onlyAudio: boolean;
      roomSid: string;
    };
  };
};

const Ringer: React.FC<Props> = ({}: Props) => {
  // useEffect(() => {
  //   InCallManager.startRingtone('_BUNDLE_');
  //   return () => {
  //     InCallManager.stopRingtone();
  //   };
  // }, []);
  // const {callUuid, roomName, roomSid, username, onlyAudio} =
  //   route?.params || {};
  // const matches = useSelector(matchesSelectors.getUserMatches);
  // const name = route.params?.roomName.split(' x ')[0];
  // const match = matches.find(item => item.matchedUser.username === name);
  const answer = () => {
    // if (callUuid) {
    //   InCallManager.stopRingtone();
    //   // TODO add flag to clear stack before navigate since we do not want to allow navigate back to the ringer view
    //   RNCallKeep.answerIncomingCall(callUuid);
    //   callNotifications.answerCall(callUuid);
    //   navigation.navigate('TwillioView', {
    //     username,
    //     roomSid,
    //     roomName,
    //     onlyAudio,
    //     callUuid,
    //     isIncoming: true,
    //     androidFullNotification: true,
    //   });
    // }
  };

  const reject = async () => {
    // if (callUuid && roomSid) {
    //   InCallManager.stopRingtone();
    //   try {
    //     await dependencyContainer.callsService.rejectCall(callUuid, roomSid);
    //     callNotifications.rejectCall(callUuid);
    //     RNCallKeep.rejectCall(callUuid);
    //     callNotifications.finishIncomingCallActivity();
    //   } catch (error) {
    //     callNotifications.rejectCall(callUuid);
    //     RNCallKeep.rejectCall(callUuid);
    //     callNotifications.finishIncomingCallActivity();
    //   }
    // }
  };
  return (
    <View style={styles.container}>
      {/*<FastImage*/}
      {/*  style={styles.callContainer}*/}
      {/*  source={{uri: match?.matchedUser?.avatarUrl}}*/}
      {/*/>*/}
      <Text numberOfLines={1}>
        {/*{username}*/}
        xxx
      </Text>
      <View style={styles.buttonContainer}>
        <TouchableOpacity
          style={[styles.optionButton, styles.redBorder]}
          onPress={reject}>
          {/*<Image*/}
          {/*  source={onlyAudio ? images.call : images.video}*/}
          {/*  style={styles.buttonIcon}*/}
          {/*/>*/}
        </TouchableOpacity>
        <TouchableOpacity style={styles.optionButton} onPress={answer}>
          {/*<Image*/}
          {/*  source={onlyAudio ? images.call : images.video}*/}
          {/*  style={styles.buttonIcon}*/}
          {/*/>*/}
        </TouchableOpacity>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {flex: 1, justifyContent: 'center', alignItems: 'center'},
  buttonContainer: {
    flexDirection: 'row',
    marginTop: 50,
    justifyContent: 'space-around',
    width: '100%',
  },
  optionButton: {
    width: 64,
    height: 64,
    marginLeft: 10,
    marginRight: 10,
    borderRadius: 32,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(255,255,255,0.60)',
    borderColor: 'green',
    borderWidth: 4,
  },
  buttonIcon: {
    width: 30,
    height: 30,
    resizeMode: 'contain',
  },
  redBorder: {
    borderColor: 'red',
  },
  callContainer: {
    flex: 1,
    position: 'absolute',
    bottom: 0,
    top: 0,
    left: 0,
    right: 0,
  },
});

export default Ringer;
