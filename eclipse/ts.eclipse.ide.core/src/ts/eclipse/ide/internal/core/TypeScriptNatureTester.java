package ts.eclipse.ide.internal.core;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;

import ts.eclipse.ide.core.resources.IIDETypeScriptProject;
import ts.eclipse.ide.core.utils.TypeScriptResourceUtil;

public class TypeScriptNatureTester extends PropertyTester {

	private static final String IS_TYPESCRIPT_PROJECT_PROPERTY = "isTypeScriptProject";
	private static final String HAS_TYPESCRIPT_BUILDER_PROPERTY = "hasTypeScriptBuilder";
	private static final String CAN_ADD_TO_BUILDPATH_PROPERTY = "canAddToBuildPath";
	private static final String CAN_REMOVE_TO_BUILDPATH_PROPERTY = "canRemoveToBuildPath";

	public TypeScriptNatureTester() {
		// Default constructor is required for property tester
	}

	/**
	 * Tests if the receiver object is a project is a TypeScript project
	 * 
	 * @return true if the receiver object is a Project that has a nature that
	 *         is treated as TypeScript nature, otherwise false is returned
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (IS_TYPESCRIPT_PROJECT_PROPERTY.equals(property)) {
			return testIsTypeScriptProject(receiver);
		} else if (HAS_TYPESCRIPT_BUILDER_PROPERTY.equals(property)) {
			return testHasTypeScriptBuilder(receiver);
		} else if (CAN_ADD_TO_BUILDPATH_PROPERTY.equals(property)) {
			return testCanAddToBuildPath(receiver);
		} else if (CAN_REMOVE_TO_BUILDPATH_PROPERTY.equals(property)) {
			return testCanRemoveToBuildPath(receiver);
		}
		return false;
	}

	private boolean testIsTypeScriptProject(Object receiver) {
		if (receiver instanceof IAdaptable) {
			IProject project = (IProject) ((IAdaptable) receiver).getAdapter(IProject.class);
			if (project != null) {
				return TypeScriptResourceUtil.isTypeScriptProject(project);
			}
		}
		return false;
	}

	private boolean testHasTypeScriptBuilder(Object receiver) {
		if (receiver instanceof IAdaptable) {
			IProject project = (IProject) ((IAdaptable) receiver).getAdapter(IProject.class);
			if (project != null) {
				return TypeScriptResourceUtil.hasTypeScriptBuilder(project);
			}
		}
		return false;
	}

	private boolean testCanAddToBuildPath(Object receiver) {
		if (receiver instanceof IAdaptable) {
			IResource resource = (IResource) ((IAdaptable) receiver).getAdapter(IResource.class);
			if (resource != null) {
				switch (resource.getType()) {
				case IResource.PROJECT:
				case IResource.FOLDER:
					return true;
				case IResource.FILE:
					return TypeScriptResourceUtil.isTsConfigFile(resource);
				}
			}
		}
		return false;
	}

	private boolean testCanRemoveToBuildPath(Object receiver) {
		IContainer container = TypeScriptResourceUtil.getBuildPathContainer(receiver);
		if (container == null) {
			return false;
		}
		try {
			IIDETypeScriptProject tsProject = TypeScriptResourceUtil.getTypeScriptProject(container.getProject());
			return tsProject.getTypeScriptBuildPath().getContainers().contains(container);

		} catch (CoreException e) {
		}
		return true;
	}

}
