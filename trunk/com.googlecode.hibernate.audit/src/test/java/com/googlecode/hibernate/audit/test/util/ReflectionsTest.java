package com.googlecode.hibernate.audit.test.util;

import org.testng.annotations.Test;
import org.apache.log4j.Logger;
import com.googlecode.hibernate.audit.util.Reflections;
import com.googlecode.hibernate.audit.test.util.data.A;
import com.googlecode.hibernate.audit.test.util.data.B;
import com.googlecode.hibernate.audit.test.util.data.C;
import com.googlecode.hibernate.audit.test.util.data.D;
import com.googlecode.hibernate.audit.test.util.data.E;
import com.googlecode.hibernate.audit.test.util.data.F1;
import com.googlecode.hibernate.audit.test.util.data.F2;
import com.googlecode.hibernate.audit.test.util.data.FParty;
import com.googlecode.hibernate.audit.test.util.data.G;
import com.googlecode.hibernate.audit.test.util.data.H;
import com.googlecode.hibernate.audit.test.util.data.I;
import com.googlecode.hibernate.audit.test.util.data.J;
import com.googlecode.hibernate.audit.test.util.data.F3;
import com.googlecode.hibernate.audit.test.util.data.K;
import com.googlecode.hibernate.audit.test.util.data.L;
import com.googlecode.hibernate.audit.test.util.data.M;
import com.googlecode.hibernate.audit.test.util.data.N;
import com.googlecode.hibernate.audit.test.util.data.NImpl;
import com.googlecode.hibernate.audit.test.util.data.ASubclass;
import com.googlecode.hibernate.audit.test.util.data.CustomImmutableInteger;
import com.googlecode.hibernate.audit.test.util.data.P;
import com.googlecode.hibernate.audit.test.util.data.PImpl;
import com.googlecode.hibernate.audit.test.util.data.Q;
import com.googlecode.hibernate.audit.test.util.data.R;
import com.googlecode.hibernate.audit.test.util.data.S;
import com.googlecode.hibernate.audit.test.util.data.ApplicationLevelImmutableImpl;
import com.googlecode.hibernate.audit.test.util.data.ApplicationLevelImmutable;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * @author <a href="mailto:ovidiu@feodorov.com">Ovidiu Feodorov</a>
 *
 * Copyright 2008 Ovidiu Feodorov
 *
 * @version <tt>$Revision$</tt>
 *
 * $Id$
 */
@Test(sequential = true)
public class ReflectionsTest
{
    // Constants -----------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(ReflectionsTest.class);

    // Static --------------------------------------------------------------------------------------

    // Attributes ----------------------------------------------------------------------------------

    // Constructors --------------------------------------------------------------------------------

    // Public --------------------------------------------------------------------------------------

    @Test(enabled = true)
    public void testMutate() throws Exception
    {
        A a = new A();

        Reflections.mutate(a, "s", "blah");
        Reflections.mutate(a, "i", new Integer(77));

        log.debug(a);

        assert "blah".equals(a.getS());
        assert new Integer(77).equals(a.getI());
    }

    @Test(enabled = true)
    public void testMutateWithSubclass() throws Exception
    {
        A a = new A();
        SubB subB = new SubB();

        Reflections.mutate(a, "b", subB);

        assert subB == a.getB();
    }

    @Test(enabled = true)
    public void testMutate_NoSuchMethodException() throws Exception
    {
        A a = new A();
        SubB subB = new SubB();

        try
        {
            Reflections.mutate(a, "blah", subB);
            throw new Error("should've failed");
        }
        catch(NoSuchMethodException e)
        {
            log.debug(e.getMessage());
        }
    }

    @Test(enabled = true)
    public void testMutate_Collection() throws Exception
    {
        A a = new A();

        Collection<Object> c = new ArrayList<Object>();
        c.add(new B("ben"));
        c.add(new B("bill"));
        c.add(new B("becky"));

        Reflections.mutateCollection(a, "bs", c);

        List<B> bs = a.getBs();

        assert bs.size() == 3;

        assert bs.remove(new B("ben"));
        assert bs.remove(new B("bill"));
        assert bs.remove(new B("becky"));
    }

    @Test(enabled = true)
    public void testMutate_Collection_NoSetMethod() throws Exception
    {
        D d = new D();

        Collection<Object> c = new ArrayList<Object>();
        c.add(new E("erica"));
        c.add(new E("ella"));
        c.add(new E("ethan"));

        Reflections.mutateCollection(d, "es", c);

        List<E> es = d.getEs();

        assert es.size() == 3;

        assert es.remove(new E("erica"));
        assert es.remove(new E("ella"));
        assert es.remove(new E("ethan"));
    }

    @Test(enabled = true)
    public void testMutate_Collection_NoSetMethod_FParties() throws Exception
    {
        D d = new D();

        Collection<Object> c = new ArrayList<Object>();
        c.add(new FParty("fenwick"));
        c.add(new FParty("fester"));
        c.add(new FParty("falwell"));

        Reflections.mutateCollection(d, "fParties", c);

        List<FParty> fParties = d.getFParties();

        assert fParties.size() == 3;

        assert fParties.remove(new FParty("fenwick"));
        assert fParties.remove(new FParty("fester"));
        assert fParties.remove(new FParty("falwell"));
    }

    @Test(enabled = true)
    public void testMutate_Collection_NoMutator_MutableCollection() throws Exception
    {
        L l = new L();

        assert l.getStrings().isEmpty();

        List<String> newsts =  new ArrayList<String>();
        newsts.add("blah");

        Reflections.mutateCollection(l, "strings", newsts);

        List<String> result = l.getStrings();
        assert result.size() == 1;
        assert result.contains("blah");
    }

    @Test(enabled = true)
    public void testMutate_PrivateEverything() throws Exception
    {
        K k = new K();

        Reflections.mutate(k, "id", new Long(10));
        Reflections.mutate(k, "name", "blah");

        assert new Long(10).equals(K.getIdFrom(k));
        assert "blah".equals(K.getNameFrom(k));
    }

    /**
     * Test case for https://jira.novaordis.org/browse/HBA-91
     */
    @Test(enabled = true)
    public void testMutate_Subclass() throws Exception
    {
        M m = new M();
        N n = new NImpl();

        Reflections.mutate(m, "n", n);

        N n2 = m.getN();
        assert n == n2;
    }

    /**
     * Test case for https://jira.novaordis.org/browse/HBA-91
     */
    @Test(enabled = true)
    public void testMutate_MutatorIsInheritedFromSuperclass() throws Exception
    {
        ASubclass asub = new ASubclass();

        Reflections.mutate(asub, "s", "blah");

        assert "blah".equals(asub.getS());
    }

    @Test(enabled = true)
    public void testDeepCopy_Null() throws Exception
    {
        assert null == Reflections.deepCopy(null);
    }

    @Test(enabled = true)
    public void testDeepCopy_NoState() throws Exception
    {
        G g = G.getInstance();
        G copy = (G)Reflections.deepCopy(g);

        assert copy != null;
        assert g != copy;
    }

    @Test(enabled = true)
    public void testDeepCopy_ReferenceToImmutable() throws Exception
    {
        G g = G.getInstance();
        String s = "blah";
        g.setString(s);

        G copy = (G)Reflections.deepCopy(g);

        assert g != copy;
        assert s == copy.getString();
    }

    @Test(enabled = true)
    public void testDeepCopy_ReferenceToMutable() throws Exception
    {
        G g = G.getInstance();

        G otherG = G.getInstance();
        otherG.setString("otherG");

        g.setG(otherG);

        G copy = (G)Reflections.deepCopy(g);

        assert g != copy;

        G otherGCopy = copy.getG();
        assert otherG != otherGCopy;
        assert "otherG".equals(otherGCopy.getString());
    }

    @Test(enabled = true)
    public void testDeepCopy_UntypedNullSet() throws Exception
    {
        G g = G.getInstance();
        G copy = (G)Reflections.deepCopy(g);

        assert copy.getStrings() == null;
    }

    @Test(enabled = true)
    public void testDeepCopy_UntypedSetOfImmutables() throws Exception
    {
        G g = G.getInstance();

        Set strings = new HashSet();
        strings.add("sone");
        strings.add("stwo");

        g.setStrings(strings);

        G copy = (G)Reflections.deepCopy(g);

        Set stringsCopy = copy.getStrings();
        assert strings != stringsCopy;

        assert 2 == stringsCopy.size();
        assert stringsCopy.contains("sone");
        assert stringsCopy.contains("stwo");
    }

    @Test(enabled = true)
    public void testDeepCopy_UntypedSetOfMutables() throws Exception
    {
        G g = G.getInstance();

        Set gs = new HashSet();
        G gone = G.getInstance();
        gone.setString("gone");
        G gtwo = G.getInstance();
        gtwo.setString("gtwo");
        gs.add(gone);
        gs.add(gtwo);
        g.setGs(gs);

        G copy = (G)Reflections.deepCopy(g);

        Set gsCopy = copy.getGs();
        assert gs != gsCopy;

        assert 2 == gsCopy.size();

        for(Object o: gsCopy)
        {
            G gl2 = (G)o;

            if ("gone".equals(gl2.getString()))
            {
                assert gl2 != gone;
            }
            else if ("gtwo".equals(gl2.getString()))
            {
                assert gl2 != gtwo;
            }
            else
            {
                throw new Error("did not expect " + gl2);
            }
        }
    }

    @Test(enabled = true)
    public void testDeepCopy_TypedSetOfImmutables() throws Exception
    {
        G g = G.getInstance();

        Set<String> typedStrings = new HashSet<String>();
        typedStrings.add("sone");
        typedStrings.add("stwo");

        g.setTypedStrings(typedStrings);

        G copy = (G)Reflections.deepCopy(g);

        Set<String> typedStringsCopy = copy.getTypedStrings();
        assert typedStrings != typedStringsCopy;

        assert 2 == typedStringsCopy.size();
        assert typedStringsCopy.contains("sone");
        assert typedStringsCopy.contains("stwo");
    }

    @Test(enabled = true)
    public void testDeepCopy_TypedSetOfMutables() throws Exception
    {
        G g = G.getInstance();

        Set<G> typedGs = new HashSet<G>();
        G gone = G.getInstance();
        gone.setString("gone");
        G gtwo = G.getInstance();
        gtwo.setString("gtwo");
        typedGs.add(gone);
        typedGs.add(gtwo);
        g.setTypedGs(typedGs);

        G copy = (G)Reflections.deepCopy(g);

        Set<G> typedGsCopy = copy.getTypedGs();
        assert typedGs != typedGsCopy;

        assert 2 == typedGsCopy.size();

        for(G gl2: typedGsCopy)
        {
            if ("gone".equals(gl2.getString()))
            {
                assert gl2 != gone;
            }
            else if ("gtwo".equals(gl2.getString()))
            {
                assert gl2 != gtwo;
            }
            else
            {
                throw new Error("did not expect " + gl2);
            }
        }
    }

    @Test(enabled = true)
    public void testDeepCopy_CircularReference() throws Exception
    {
        A a = new A();
        B b = new B();

        a.setB(b);
        b.setA(a);

        A aCopy = (A)Reflections.deepCopy(a);

        assert a != aCopy;

        B bCopy = aCopy.getB();
        assert b != bCopy;

        assert aCopy == bCopy.getA();
    }

    @Test(enabled = true)
    public void testDeepCopy_CircularReferencesViaACollection() throws Exception
    {
        A a = new A();
        B b = new B();

        List<B> bs = new ArrayList<B>();
        bs.add(b);
        a.setBs(bs);
        b.setA(a);

        A aCopy = (A)Reflections.deepCopy(a);

        assert a != aCopy;

        List<B> bsCopy = aCopy.getBs();
        assert a.getBs() != bsCopy;

        assert bsCopy.size() == 1;

        B bCopy = bsCopy.get(0);
        assert b != bCopy;

        assert aCopy == bCopy.getA();
    }

    @Test(enabled = true)
    public void testDeepCopy_CircularReferencesViaHibernateProxy() throws Exception
    {
        PImpl p = new PImpl();
        Q q = new Q();
        p.setQ(q);
        q.setP(p.createHibernateProxyOfMyself());

        PImpl pCopy = (PImpl)Reflections.deepCopy(p);

        assert pCopy != p;

        Q qCopy = pCopy.getQ();

        assert qCopy != q;

        // this makes sure the proxies are cleaned out, but referential integrity maintained

        assert pCopy == qCopy.getP();
    }

    @Test(enabled = true)
    public void testDeepCopy_RemoveHibernateProxy() throws Exception
    {
        Q q = new Q();
        PImpl p = new PImpl();
        q.setP(p.createHibernateProxyOfMyself());

        Q qCopy = (Q)Reflections.deepCopy(q);

        assert qCopy != q;

        P pCopy = qCopy.getP();

        assert pCopy != p;
        assert pCopy instanceof PImpl; // HibernateProxy was stripped off
    }

    @Test(enabled = true)
    public void testDeepCopy_RemoveHibernateProxy2() throws Exception
    {
        PImpl p = new PImpl();
        p.setI(789125);
        Object proxy = p.createHibernateProxyOfMyself();

        Object copy = Reflections.deepCopy(proxy);

        assert copy != p;
        assert 789125 == ((PImpl)copy).getI();
    }

    @Test(enabled = true)
    public void testApplyDelta() throws Exception
    {

        A base = new A();

        A delta = new A();

        delta.setS("delta");
        delta.setI(7);
        delta.setBo(true);

        B b = new B("ben");
        delta.setB(b);

        B b1 = new B("bob");
        B b2 = new B("bill");
        List<B> bs = new ArrayList<B>();
        bs.add(b1);
        bs.add(b2);
        delta.setBs(bs);

        Reflections.applyDelta(base, delta);

        assert base != delta;

        assert "delta".equals(base.getS());
        assert new Integer(7).equals(base.getI());
        assert base.isBo();

        B bCopy = base.getB();

        assert b != bCopy;
        assert "ben".equals(bCopy.getS());

        List<B> bsresult = base.getBs();
        assert bsresult != bs;

        assert 2 == bsresult.size();

        for(B bl2: bsresult)
        {
            if ("bob".equals(bl2.getS()))
            {
                assert b1 != bl2;
            }
            else if ("bill".equals(bl2.getS()))
            {
                assert b2 != bl2;
            }
            else
            {
                throw new Error("unexpected " + bl2);
            }
        }
    }

    @Test(enabled = true)
    public void testApplyDelta_DeltaContainsReferenceToItself() throws Exception
    {
        A delta = new A();
        delta.setS("anna");

        B b = new B("ben");
        b.setA(delta);
        delta.setB(b);

        A base = new A();
        Reflections.applyDelta(base, delta);

        assert base.getB().getA() == base;
    }

    @Test(enabled = true)
    public void testApplyDelta_DeltaContainsReferenceToItselfInACollection() throws Exception
    {
        A delta = new A();
        delta.setS("anna");

        C c = new C("cami");
        c.getAs().add(delta);
        delta.setC(c);

        A base = new A();
        Reflections.applyDelta(base, delta);

        assert "anna".equals(base.getS());

        C cResult = base.getC();

        assert c != cResult;

        assert "cami".equals(cResult.getS());

        List<A> as = cResult.getAs();
        assert as.size() == 1;

        assert base == as.get(0);
    }

    @Test(enabled = true)
    public void testApplyDelta_DeltaContainsReferenceToItself_MultipleLevels() throws Exception
    {
        H h = new H();
        I i = new I();
        h.setI(i);
        J j = new J();
        i.setJ(j);
        j.setH(h);


        H base = new H();
        Reflections.applyDelta(base, h);

        I i2 = base.getI();
        assert i != i2;

        J j2 = i2.getJ();
        assert j != j2;

        H h2 = j2.getH();
        assert base == h2;
    }

    @Test(enabled = false) // TEST_OFF
    public void testApplyDelta_ImmutableUntypedEmptyCollectionWithAddMethod() throws Exception
    {
        // first make sure the collection is immutable

        F1 delta = new F1();

        Collection ucstrings = delta.getUcstrings();

        try
        {
            ucstrings.add("blah");
            throw new Error("should've failed");
        }
        catch(UnsupportedOperationException e)
        {
            // ok
        }

        F1 base = new F1();
        base.addUcstring("blah");
        assert 1 == base.getUcstrings().size();

        Reflections.applyDelta(base, delta);

        Collection c = base.getUcstrings();
        assert c.isEmpty();
    }

    @Test(enabled = true)
    public void testApplyDelta_ImmutableUntypedCollectionWithAddMethod_OneMember() throws Exception
    {
        // first make sure the collection is immutable

        F1 delta = new F1();

        Collection ucstrings = delta.getUcstrings();

        try
        {
            ucstrings.add("blah");
            throw new Error("should've failed");
        }
        catch(UnsupportedOperationException e)
        {
            // ok
        }

        F1 base = new F1();
        assert base.getUcstrings().isEmpty();

        delta.addUcstring("something");

        Reflections.applyDelta(base, delta);

        Collection c = base.getUcstrings();
        assert 1 == c.size();
        assert c.contains("something");
    }

    @Test(enabled = true)
    public void testApplyDelta_ImmutableUntypedCollectionWithAddMethod_HeterogeneousCollection()
        throws Exception
    {
        // first make sure the collection is immutable

        F1 delta = new F1();

        Collection ucstrings = delta.getUcstrings();

        try
        {
            ucstrings.add("blah");
            throw new Error("should've failed");
        }
        catch(UnsupportedOperationException e)
        {
            // ok
        }

        F1 base = new F1();
        assert base.getUcstrings().isEmpty();

        delta.addUcstring("something");

        // we cheat for the sake of the test
        delta.genericAdd(new Integer(7));

        try
        {
            Reflections.applyDelta(base, delta);
            throw new Error("should've failed");
        }
        catch(IllegalArgumentException e)
        {
            log.debug(e.getMessage());
        }
    }

    @Test(enabled = false) // TEST_OFF
    public void testApplyDelta_ImmutableUntypedEmptySetWithAddMethod() throws Exception
    {
        // first make sure the collection is immutable

        F2 delta = new F2();

        Set usstrings = delta.getUsstrings();

        try
        {
            usstrings.add("blah");
            throw new Error("should've failed");
        }
        catch(UnsupportedOperationException e)
        {
            // ok
        }

        F2 base = new F2();
        base.addUsstring("blah");
        assert 1 == base.getUsstrings().size();
        assert delta.getUsstrings().isEmpty();

        Reflections.applyDelta(base, delta);

        Collection c = base.getUsstrings();
        assert c.isEmpty();
    }

    @Test(enabled = true)
    public void testApplyDelta_ImmutableUntypedSetWithAddMethod_OneMember() throws Exception
    {
        // first make sure the collection is immutable

        F2 delta = new F2();

        Set usstrings = delta.getUsstrings();

        try
        {
            usstrings.add("blah");
            throw new Error("should've failed");
        }
        catch(UnsupportedOperationException e)
        {
            // ok
        }

        F2 base = new F2();
        assert base.getUsstrings().isEmpty();

        delta.addUsstring("something");

        Reflections.applyDelta(base, delta);

        Set s = base.getUsstrings();
        assert 1 == s.size();
        assert s.contains("something");
    }

    @Test(enabled = true)
    public void testApplyDelta_ImmutableUntypedSetWithAddMethod_HeterogeneousCollection()
        throws Exception
    {
        // first make sure the collection is immutable

        F2 delta = new F2();

        Set usstrings = delta.getUsstrings();

        try
        {
            usstrings.add("blah");
            throw new Error("should've failed");
        }
        catch(UnsupportedOperationException e)
        {
            // ok
        }

        F2 base = new F2();
        assert base.getUsstrings().isEmpty();

        delta.addUsstring("something");

        // we cheat for the sake of the test
        delta.genericAdd(new Integer(7));

        try
        {
            Reflections.applyDelta(base, delta);
            throw new Error("should've failed");
        }
        catch(IllegalArgumentException e)
        {
            log.debug(e.getMessage());
        }
    }

    @Test(enabled = false) // TEST_OFF
    public void testApplyDelta_ImmutableCollectionWithAddMethod_Empty() throws Exception
    {
        // first make sure the collection is immutable

        F3 delta = new F3();

        Set<String> strings = delta.getStrings();

        try
        {
            strings.add("blah");
            throw new Error("should've failed");
        }
        catch(UnsupportedOperationException e)
        {
            // ok
        }

        F3 base = new F3();
        base.addString("blah");

        Reflections.applyDelta(base, delta);

        strings = base.getStrings();
        assert strings.isEmpty();
    }

    @Test(enabled = true)
    public void testApplyDelta_ImmutableCollectionWithAddMethod() throws Exception
    {
        // first make sure the collection is immutable

        F3 delta = new F3();

        Set<String> strings = delta.getStrings();

        try
        {
            strings.add("blah");
            throw new Error("should've failed");
        }
        catch(UnsupportedOperationException e)
        {
            // ok
        }

        delta.addString("alice");
        delta.addString("bob");

        F3 base = new F3();

        Reflections.applyDelta(base, delta);

        Set<String> stringsCopy = base.getStrings();

        assert strings != stringsCopy;

        assert 2 == strings.size();

        assert strings.contains("alice");
        assert strings.contains("bob");
    }

    @Test(enabled = true)
    public void testApplyDelta_Collection_NoSetMethod_FParties() throws Exception
    {
        D d = new D();
        d.addFParty(new FParty("fenwick"));
        d.addFParty(new FParty("fester"));
        d.addFParty(new FParty("falwell"));

        D base = new D();
        Reflections.applyDelta(base, d);

        List<FParty> fParties = base.getFParties();

        assert fParties.size() == 3;

        assert fParties.remove(new FParty("fenwick"));
        assert fParties.remove(new FParty("fester"));
        assert fParties.remove(new FParty("falwell"));
    }

    @Test(enabled = true)
    public void testApplyDelta_Collection_NoMutator() throws Exception
    {
        L l = new L();
        l.getStrings().add("blah");

        L base = new L();
        Reflections.applyDelta(base, l);

        List<String> result = base.getStrings();
        assert result.size() == 1;
        assert result.contains("blah");
    }

    @Test(enabled = true)
    public void testApplyDelta_Collection_NoMutator_CorrectDeepCopy() throws Exception
    {
        L l = new L();
        A a = new A();
        a.setS("saa");
        l.getAs().add(a);

        L base = new L();
        Reflections.applyDelta(base, l);

        assert base.getStrings().isEmpty();

        List<A> as = base.getAs();
        assert as.size() == 1;

        A aCopy = as.get(0);

        assert a != aCopy;
        assert "saa".equals(aCopy.getS());
    }

    @Test(enabled = true)
    public void testApplyDelta_Collection_NoMutator_EmptyDelta() throws Exception
    {
        L delta = new L();

        L base = new L();
        A a = new A();
        a.setS("saa");
        base.getAs().add(a);

        Reflections.applyDelta(base, delta);

        assert base.getStrings().isEmpty();
        assert base.getAs().isEmpty();
    }

    @Test(enabled = true)
    public void testApplyDelta_Collection_NoMutator_ProprietaryList() throws Exception
    {
        L delta = new L();
        B b = new B();
        b.setS("baa");
        delta.getBs().add(b);

        L base = new L();

        Reflections.applyDelta(base, delta);

        assert base.getStrings().isEmpty();
        assert base.getAs().isEmpty();

        List<B> result = base.getBs();

        assert result.size() == 1;
        B bCopy = result.get(0);
        assert b != bCopy;
        assert "baa".equals(bCopy.getS());
    }

    @Test(enabled = true)
    public void testIsMuttable_CustomImmutableInteger() throws Exception
    {
        CustomImmutableInteger i = new CustomImmutableInteger(1);

        assert !Reflections.isMutable(i);
    }

    @Test(enabled = true)
    public void testFind_Itself() throws Exception
    {
        R r = new R();
        Long id = new Long(234345l);
        r.setId(id);

        Object o = Reflections.find(r, R.class, id);
        assert o == r;
    }

    @Test(enabled = true)
    public void testFind_Collection() throws Exception
    {
        R r = new R();
        Long id = new Long(334345l);
        r.setId(id);

        Set<R> set = new HashSet<R>();

        set.add(new R());
        set.add(new R(2345342345l));
        set.add(r);
        set.add(new R(23l));

        Object o = Reflections.find(set, R.class, id);
        assert o == r;
    }

    @Test(enabled = true)
    public void testFind_SubCollection() throws Exception
    {
        R r = new R();
        Long id = new Long(334345l);
        r.setId(id);

        S s = new S();
        Set<R> rs = s.getRs();

        rs.add(new R());
        rs.add(new R(2345342345l));
        rs.add(r);
        rs.add(new R(23l));

        Object o = Reflections.find(s, R.class, id);
        assert o == r;
    }

    @Test(enabled = true)
    public void testFind() throws Exception
    {
        A a = new A();
        R r = new R();

        r.setId(34234l);

        a.getRs().add(r);

        Object o = Reflections.find(a, r.getClass(), r.getId());

        assert o == r;
    }

    @Test(enabled = true)
    public void testApplicationLevelImmutableClass() throws Exception
    {
        assert Reflections.getImmutableClasses().isEmpty();

        assert Reflections.registerImmutableClass(ApplicationLevelImmutableImpl.class);
        assert !Reflections.registerImmutableClass(ApplicationLevelImmutableImpl.class);

        assert Reflections.getImmutableClasses().size() == 1;

        ApplicationLevelImmutableImpl immutable = new ApplicationLevelImmutableImpl();

        assert !Reflections.isMutable(immutable);
        Object o = Reflections.deepCopy(immutable);
        assert o == immutable;

        assert Reflections.unregisterImmutableClass(ApplicationLevelImmutableImpl.class);
        assert !Reflections.unregisterImmutableClass(ApplicationLevelImmutableImpl.class);

        assert Reflections.getImmutableClasses().isEmpty();
    }

    @Test(enabled = true)
    public void testApplicationLevelImmutableInterface() throws Exception
    {
        assert Reflections.getImmutableClasses().isEmpty();

        assert Reflections.registerImmutableClass(ApplicationLevelImmutable.class);
        assert !Reflections.registerImmutableClass(ApplicationLevelImmutable.class);

        assert Reflections.getImmutableClasses().size() == 1;

        ApplicationLevelImmutableImpl immutable = new ApplicationLevelImmutableImpl();

        assert !Reflections.isMutable(immutable);
        Object o = Reflections.deepCopy(immutable);
        assert o == immutable;

        assert Reflections.unregisterImmutableClass(ApplicationLevelImmutable.class);
        assert !Reflections.unregisterImmutableClass(ApplicationLevelImmutable.class);

        assert Reflections.getImmutableClasses().isEmpty();
    }

    // Package protected ---------------------------------------------------------------------------

    // Protected -----------------------------------------------------------------------------------

    // Private -------------------------------------------------------------------------------------

    // Inner classes -------------------------------------------------------------------------------

}
