import React, {useEffect} from 'react';
import {View, Text, StyleSheet, Image} from 'react-native';
import {TouchableOpacity} from 'react-native-gesture-handler';
import images from './assets/images';
import Icon from 'react-native-vector-icons/FontAwesome5';
import {AndroidCallStackProps} from './AndroidCallActivity';
import InCallManager from 'react-native-incall-manager';
import callNotifications from './CallNotifications';

type Props = AndroidCallStackProps<'Ringer'>;

const Ringer: React.FC<Props> = ({route: {params}}) => {
  const username = params.username;
  const callUuid = params.callUuid;

  useEffect(() => {
    InCallManager.startRingtone('_BUNDLE_');
    return () => {
      InCallManager.stopRingtone();
    };
  }, []);
  // const {callUuid, roomName, roomSid, username, onlyAudio} =
  //   route?.params || {};
  // const matches = useSelector(matchesSelectors.getUserMatches);
  // const name = route.params?.roomName.split(' x ')[0];
  // const match = matches.find(item => item.matchedUser.username === name);
  const answer = () => {
    // if (callUuid) {
    InCallManager.stopRingtone();
    //   // TODO add flag to clear stack before navigate since we do not want to allow navigate back to the ringer view
    //   RNCallKeep.answerIncomingCall(callUuid);
    callNotifications.answerCall(callUuid);
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
    InCallManager.stopRingtone();
    //   try {
    //     await dependencyContainer.callsService.rejectCall(callUuid, roomSid);
    callNotifications.rejectCall(callUuid);
    //     RNCallKeep.rejectCall(callUuid);
    //     callNotifications.finishIncomingCallActivity();
    //   } catch (error) {
    //     callNotifications.rejectCall(callUuid);
    //     RNCallKeep.rejectCall(callUuid);
    callNotifications.finishIncomingCallActivity();
    //   }
    // }
  };
  return (
    <View style={styles.container}>
      <View style={styles.userDetailsContainer}>
        <Text numberOfLines={1} style={styles.userName}>
          {username}
        </Text>
        <Image style={styles.callAvatar} source={images.avatarPlaceholder} />
      </View>
      <View style={styles.buttonContainer}>
        <TouchableOpacity
          style={[styles.optionButton, styles.redBorder]}
          onPress={reject}>
          <Icon color={'red'} name={'phone-slash'} size={32} />
        </TouchableOpacity>
        <TouchableOpacity style={styles.optionButton} onPress={answer}>
          <Icon color={'green'} name={'phone'} size={32} />
        </TouchableOpacity>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {flex: 1, justifyContent: 'space-between', alignItems: 'center'},
  buttonContainer: {
    flexDirection: 'row',
    marginBottom: 50,
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
  callAvatar: {
    height: undefined,
    width: '40%',
    aspectRatio: 1,
    borderRadius: 100,
    borderColor: '#c9c9c9',
    borderWidth: 4,
    marginVertical: 16,
  },
  userName: {
    fontWeight: 'bold',
    fontSize: 40,
  },
  userDetailsContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingBottom: '15%',
    flex: 1,
  },
});

export default Ringer;
