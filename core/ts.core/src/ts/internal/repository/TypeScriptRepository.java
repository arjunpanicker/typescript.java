package ts.internal.repository;

import java.io.File;

import ts.repository.ITypeScriptRepository;
import ts.repository.TypeScriptRepositoryException;
import ts.repository.TypeScriptRepositoryManager;
import ts.utils.FileUtils;

/**
 *
 *
 */
public class TypeScriptRepository implements ITypeScriptRepository {

	private final TypeScriptRepositoryManager manager;
	private File baseDir;
	private String name;
	private File typesScriptDir;
	private File tscFile;
	private File tsserverFile;
	private File tslintFile;
	private String tslintName;
	private String typesScriptVersion;
	private String tslintVersion;

	public TypeScriptRepository(File baseDir) throws TypeScriptRepositoryException {
		this(baseDir, null);
	}

	public TypeScriptRepository(File baseDir, TypeScriptRepositoryManager manager)
			throws TypeScriptRepositoryException {
		this.manager = manager;
		this.baseDir = baseDir;
		updateBaseDir(baseDir);
	}

	private void updateBaseDir(File baseDir) throws TypeScriptRepositoryException {
		this.typesScriptDir = baseDir;
		// tsserver file
		this.tsserverFile = TypeScriptRepositoryManager.getTsserverFile(typesScriptDir);
		if (!tsserverFile.exists()) {
			this.typesScriptDir = new File(baseDir, "node_modules/typescript");
			this.tsserverFile = TypeScriptRepositoryManager.getTsserverFile(typesScriptDir);
		}
		if (!tsserverFile.exists()) {
			throw new TypeScriptRepositoryException(FileUtils.getPath(typesScriptDir)
					+ " is not a valid TypeScript repository. Check the directory contains node_modules/typescript/bin/tsserver or bin/tsserver.");
		}
		// tsc file
		this.tscFile = TypeScriptRepositoryManager.getTscFile(typesScriptDir);
		this.typesScriptVersion = TypeScriptRepositoryManager.getPackageJsonVersion(typesScriptDir);
		this.setName(generateName("TypeScript", typesScriptVersion));
		// tslint file
		File tslintBaseDir = new File(baseDir, "node_modules/tslint");
		if (tslintBaseDir.exists()) {
			this.tslintFile = TypeScriptRepositoryManager.getTslintFile(tslintBaseDir);
			this.tslintVersion = TypeScriptRepositoryManager.getPackageJsonVersion(tslintBaseDir);
			this.tslintName = generateName("tslint", tslintVersion);
		}
	}

	private String generateName(String prefix, String version) {
		StringBuilder name = new StringBuilder(prefix);
		name.append(" (");
		if (version != null) {
			name.append(version);
		}
		name.append(")");
		return name.toString();
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) throws TypeScriptRepositoryException {
		ITypeScriptRepository repository = manager != null ? manager.getRepository(name) : null;
		if (repository == null || repository.equals(this)) {
			this.name = name;
		} else {
			throw new TypeScriptRepositoryException("It already exists a TypeScript repository with the name " + name);
		}
	}

	@Override
	public File getBaseDir() {
		return baseDir;
	}

	@Override
	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}

	@Override
	public String getTypesScriptVersion() {
		return typesScriptVersion;
	}

	@Override
	public File getTscFile() {
		return tscFile;
	}

	@Override
	public File getTsserverFile() {
		return tsserverFile;
	}

	@Override
	public String getTslintVersion() {
		return tslintVersion;
	}

	@Override
	public File getTslintFile() {
		return tslintFile;
	}

	@Override
	public String getTslintName() {
		return tslintName;
	}

}
