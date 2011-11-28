package com.all.client.devices;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.action.ValueAction;
import com.all.appControl.ActionMethod;
import com.all.appControl.RequestMethod;
import com.all.appControl.control.ControlEngine;
import com.all.client.model.DeviceBase;
import com.all.core.actions.Actions;
import com.all.core.actions.DevicesCopyAction;
import com.all.core.common.services.reporting.Reporter;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.event.ValueEvent;
import com.all.messengine.MessEngine;
import com.all.messengine.MessageMethod;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.User;
import com.all.shared.newsfeed.DeviceExportFeed;
import com.all.shared.stats.FeedStat;

@Service
public class DevicesService {
	private Log log = LogFactory.getLog(this.getClass());

	@Autowired
	private ControlEngine controlEngine;

	private List<DeviceBase> devices = new LinkedList<DeviceBase>();

	@Autowired
	private MessEngine messEngine;
	@Autowired
	private Reporter reporter;

	private DeviceCopyWorker copyWorker;

	public DevicesService() {
	}

	public void addDevice(DeviceBase dev) {
		if (dev != null) {
			log.debug("Adding Device: " + dev.getDeviceRoot().getName() + " To app.");
			synchronized (devices) {
				if (!devices.contains(dev)) {
					devices.add(dev);
				}
			}
			controlEngine.fireEvent(Events.Devices.ADD_DEVICE, new ValueEvent<DeviceBase>(dev));
		}
	}

	public void removeDevice(DeviceBase dev) {
		if (devices.contains(dev)) {
			synchronized (devices) {
				devices.remove(dev);
			}
			controlEngine.fireEvent(Events.Devices.REMOVE_DEVICE, new ValueEvent<DeviceBase>(dev));
		}
	}

	@RequestMethod(Actions.Devices.GET_DEVICES_ID)
	public List<DeviceBase> getDevices(Void v) {
		synchronized (devices) {
			return new ArrayList<DeviceBase>(devices);
		}
	}

	@MessageMethod(MessEngineConstants.SET_DEVICES_TYPE)
	public void setDevicesType(AllMessage<List<DeviceBase>> message) {
		List<DeviceBase> devs = message.getBody();
		for (Iterator<DeviceBase> iterator = devs.iterator(); iterator.hasNext();) {
			DeviceBase deviceBase = iterator.next();
			addDevice(deviceBase);
		}
	}

	@MessageMethod(MessEngineConstants.ADD_DEVICE_TYPE)
	public void addDeviceType(AllMessage<DeviceBase> message) {
		DeviceBase deviceBase = message.getBody();
		addDevice(deviceBase);
	}

	@MessageMethod(MessEngineConstants.REMOVE_DEVICE_TYPE)
	public void removeDeviceType(AllMessage<DeviceBase> message) {
		DeviceBase deviceBase = message.getBody();
		removeDevice(deviceBase);
	}

	@ActionMethod(Actions.Devices.DISCONECT_ID)
	public void disconnect(ValueAction<DeviceBase> valueAction) {
		messEngine.send(new AllMessage<DeviceBase>(MessEngineConstants.DISCONNEC_DEVICE_TYPE, valueAction.getValue()));
	}

	private void deleteFile(File file) {
		if (file.isDirectory()) {
			for (File file2 : file.listFiles()) {
				deleteFile(file2);
			}
		}
		try {
			file.delete();
		} catch (Exception e) {
		}
	}

	@ActionMethod(Actions.Devices.DELETE_ID)
	public void delete(ValueAction<List<File>> valueAction) {
		final List<File> files = valueAction.getValue();
		for (File file : files) {
			deleteFile(file);
		}
	}

	@ActionMethod(Actions.Devices.COPY_ID)
	public void copy(DevicesCopyAction action) {
		copyWorker = new DeviceCopyWorker(action.getModel(), action.getFile(), controlEngine.get(Model.TRACK_REPOSITORY),
				controlEngine);
		User user = controlEngine.get(Model.CURRENT_USER);
		reporter.log(new FeedStat(new DeviceExportFeed(new ContactInfo(user), action.getModel())));
		copyWorker.doInBackground();
	}

	@ActionMethod(Actions.Devices.CANCEL_COPY_ID)
	public void cancelCopy() {
		if (copyWorker != null) {
			copyWorker.cancel();
		}
	}
}
