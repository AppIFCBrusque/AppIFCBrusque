package com.ifcbrusque.app.di.component;

import com.ifcbrusque.app.di.PerService;
import com.ifcbrusque.app.di.module.ServiceModule;
import com.ifcbrusque.app.service.SyncService;

import dagger.Component;

@PerService
@Component(dependencies = ApplicationComponent.class, modules = ServiceModule.class)
public interface ServiceComponent {
    void inject(SyncService service);
}