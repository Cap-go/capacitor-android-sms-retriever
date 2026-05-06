import { WebPlugin } from '@capacitor/core';

import type {
  AndroidSmsRetrieverPlugin,
  GetHashStringResult,
  GetPhoneNumberResult,
  PluginVersionResult,
  StartWatchResult,
  StopWatchResult,
} from './definitions';

export class AndroidSmsRetrieverWeb extends WebPlugin implements AndroidSmsRetrieverPlugin {
  async startWatch(): Promise<StartWatchResult> {
    throw this.unimplemented('startWatch is only available on Android.');
  }

  async stopWatch(): Promise<StopWatchResult> {
    throw this.unimplemented('stopWatch is only available on Android.');
  }

  async getHashString(): Promise<GetHashStringResult> {
    throw this.unimplemented('getHashString is only available on Android.');
  }

  async getPhoneNumber(): Promise<GetPhoneNumberResult> {
    throw this.unimplemented('getPhoneNumber is only available on Android.');
  }

  async getPluginVersion(): Promise<PluginVersionResult> {
    return {
      version: 'web',
    };
  }
}
