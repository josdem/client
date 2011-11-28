/**
 * 
 */
package com.all.client.view;

public enum DeviceLoaderStates {
	LOADING() {
		@Override
		public String toString() {
			return "LOADING...";
		}
	},
	EMPTY() {
		public String toString() {
			return "";
		}
	},
	UNNAMED() {
		public String toString() {
			return "";
		};
	}
}