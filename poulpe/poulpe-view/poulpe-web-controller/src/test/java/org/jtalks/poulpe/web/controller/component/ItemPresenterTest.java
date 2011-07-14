/**
 * 
 */
package org.jtalks.poulpe.web.controller.component;

import static org.mockito.Mockito.reset;

import java.util.List;

import org.jtalks.poulpe.model.entity.Component;
import org.jtalks.poulpe.service.ComponentService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;


/**
 * @author Dmitriy Sukharev
 *
 */
public class ItemPresenterTest {
    
    private ItemPresenter presenter = new ItemPresenter();

    @Mock
    ComponentService componentService;
    @Mock
    ItemView view;
    @Captor
    ArgumentCaptor<List<Component>> componentCaptor;

    @BeforeClass
    protected void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter.setComponentService(componentService);
        presenter.initView(view);
    }
    
    @BeforeMethod
    public void before() {
        reset(componentService);
        reset(view);
    }
}
