package me.n1ar4.jar.obfuscator.base;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("unused")
public class ClassReference {
    private final String name;
    private final String superClass;
    private final List<String> interfaces;
    private final boolean isInterface;
    private final boolean isEnum;
    private final List<Member> members;
    private final Set<String> annotations;
    private final String jar;

    public static class Member {
        private final String name;
        private final int modifiers;
        private final Handle type;

        public Member(String name, int modifiers, Handle type) {
            this.name = name;
            this.modifiers = modifiers;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public int getModifiers() {
            return modifiers;
        }

        public Handle getType() {
            return type;
        }
    }

    public ClassReference(String name, String superClass, List<String> interfaces,
                          boolean isInterface,boolean isEnum, List<Member> members, Set<String> annotations, String jar) {
        this.name = name;
        this.superClass = superClass;
        this.interfaces = interfaces;
        this.isInterface = isInterface;
        this.isEnum = isEnum;
        this.members = members;
        this.annotations = annotations;
        this.jar = jar;
    }

    public String getJar() {
        return jar;
    }

    public String getName() {
        return name;
    }

    public String getSuperClass() {
        return superClass;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public boolean isEnum() {
        return isEnum;
    }

    public List<Member> getMembers() {
        return members;
    }

    public Handle getHandle() {
        return new Handle(name);
    }

    public Set<String> getAnnotations() {
        return annotations;
    }

    public static class Handle {
        private final String name;

        public Handle(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Handle handle = (Handle) o;
            return Objects.equals(name, handle.name);
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClassReference c = (ClassReference) o;
        return Objects.equals(name, c.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
